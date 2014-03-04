package com.myzone.reactive.stream;

import com.myzone.reactive.collection.ObservableIterable;
import com.myzone.reactive.events.ImmutableReferenceChangeEvent;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.observable.Observable;
import com.myzone.reactive.observable.ObservableHelper;
import com.myzone.reactive.stream.collectors.ObservableCollectors;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.myzone.reactive.observable.Observables.newObservableHelper;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author myzone
 * @date 13.01.14.
 */
public class ObservableStreamContinuousTest {

    private Observable.ChangeListener<String, ReferenceChangeEvent<String>> changeListenerMock;
    private ObservableHelper<String, ReferenceChangeEvent<String>> helper;
    private List<String> list;

    @Before
    public void setUp() throws Exception {
        changeListenerMock = mock(Observable.ChangeListener.class);
        helper = newObservableHelper();
        list = new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
        }};

        ObservableIterable<String, ReferenceChangeEvent<String>> is = new AbstractObservableStream<String>(helper) {
            public @Override Iterator<String> iterator() {
                return list.iterator();
            }
        }.filter(integer -> Integer.parseInt(integer) % 2 != 0)
                .collect(ObservableCollectors.<String>toObservableIterable());
        is.addListener(changeListenerMock);
    }

    @Test
    public void test1() throws Exception {
        list.add("4");
        helper.fireEvent(ImmutableReferenceChangeEvent.of(null, "4"));
        verify(changeListenerMock, never()).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

    @Test
    public void test2() throws Exception {
        list.add("5");
        helper.fireEvent(ImmutableReferenceChangeEvent.of(null, "5"));
        verify(changeListenerMock).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

    @Test
    public void test3() throws Exception {
        list.remove("4");
        helper.fireEvent(ImmutableReferenceChangeEvent.of("4", null));
        verify(changeListenerMock, never()).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

    @Test
    public void test4() throws Exception {
        list.remove("5");
        helper.fireEvent(ImmutableReferenceChangeEvent.of("5", null));
        verify(changeListenerMock).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

    @Test
    public void test5() throws Exception {
        list.remove("3");
        list.add("4");
        helper.fireEvent(ImmutableReferenceChangeEvent.of("3", "4"));
        verify(changeListenerMock).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

    @Test
    public void test6() throws Exception {
        list.remove("2");
        list.add("4");
        helper.fireEvent(ImmutableReferenceChangeEvent.of("2", "4"));
        verify(changeListenerMock, never()).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

    @Test
    public void test7() throws Exception {
        list.remove("3");
        list.add("5");
        helper.fireEvent(ImmutableReferenceChangeEvent.of("3", "5"));
        verify(changeListenerMock).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

    @Test
    public void test8() throws Exception {
        list.remove("2");
        list.add("5");
        helper.fireEvent(ImmutableReferenceChangeEvent.of("2", "5"));
        verify(changeListenerMock).onChange(any(Observable.class), any(ReferenceChangeEvent.class));
    }

}
