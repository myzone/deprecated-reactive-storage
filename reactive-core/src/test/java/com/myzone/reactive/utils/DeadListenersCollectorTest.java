package com.myzone.reactive.utils;

import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.observable.Observable;
import com.myzone.reactive.reference.ConcurrentObservableReference;
import com.myzone.reactive.reference.ObservableReadonlyReference;
import com.myzone.reactive.reference.ObservableReference;
import org.junit.Before;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.myzone.reactive.observable.Observable.ChangeListener;
import static org.mockito.Mockito.*;

/**
 * @author myzone
 * @date 03.01.14
 */
public class DeadListenersCollectorTest {

    private DeadListenersCollector deadListenersCollector;

    @Before
    public void setUp() throws Exception {
        deadListenersCollector = new DeadListenersCollector();
    }

    @Test
    public void testCollect() throws Exception {
        Consumer onListenerRemoved = mock(Consumer.class);

        ObservableReference<String, ReferenceChangeEvent<String>> subscriber = new ConcurrentObservableReference<>();
        ObservableReadonlyReference<String, ReferenceChangeEvent<String>> publisher = new ConcurrentObservableReference<String>() {
            @Override
            public void removeListener(ChangeListener<String, ? super ReferenceChangeEvent<String>> changeListener) {
                onListenerRemoved.accept(changeListener);

                super.removeListener(changeListener);
            }
        };

        WeakReference<ObservableReference<String, ReferenceChangeEvent<String>>> weakSubscriber = new WeakReference<>(subscriber);
        ChangeListener<String, ReferenceChangeEvent<String>> changeListener = spy(new ChangeListener<String, ReferenceChangeEvent<String>>() {
            @Override
            public void onChange(Observable<String, ? extends ReferenceChangeEvent<String>> source, ReferenceChangeEvent<String> event) {
                ObservableReference<String, ReferenceChangeEvent<String>> strongSubscriber = weakSubscriber.get();

                if (strongSubscriber != null) {
                    strongSubscriber.set(event.getNew());
                }
            }
        });

        subscriber.set(publisher.get());
        publisher.addListener(changeListener);
        deadListenersCollector.collect(changeListener).afterDeathOf(weakSubscriber).via(publisher::removeListener);

        subscriber = null; // let GC to handle this

        for (int i = 0; weakSubscriber.get() != null; i++) {
            if (i > 100)
                throw new Exception();

            System.gc(); // force gc
        }

        verify(changeListener, never()).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
        verify(onListenerRemoved, timeout((int) TimeUnit.SECONDS
                .toMillis(10))).accept(same(changeListener)); // let's give GC some time for collecting
    }

}
