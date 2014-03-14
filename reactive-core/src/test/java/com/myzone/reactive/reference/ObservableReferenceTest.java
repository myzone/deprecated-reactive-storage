package com.myzone.reactive.reference;

import com.google.common.base.Supplier;
import com.myzone.reactive.event.ChangeEvent;
import com.myzone.reactive.event.ReferenceChangeEvent;
import com.myzone.reactive.observable.Observable;
import com.myzone.utils.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

import static com.myzone.reactive.observable.Observable.ChangeListener;
import static com.myzone.utils.Matchers.TransformationMatcher.namedTransformation;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * @author myzone
 * @date 30.12.13
 */
public abstract class ObservableReferenceTest {

    private final Supplier<ObservableReference<String, ReferenceChangeEvent<String>>> observableReferenceFactory;

    public ObservableReferenceTest(Supplier<ObservableReference<String, ReferenceChangeEvent<String>>> observableReferenceFactory) {
        this.observableReferenceFactory = observableReferenceFactory;
    }

    @Test
    public void testUpCast() {
        ChangeListener<String, ReferenceChangeEvent<String>> changeListener1 = new ChangeListener<String, ReferenceChangeEvent<String>>() {
            @Override
            public void onChange(Observable<String, ? extends ReferenceChangeEvent<String>> source, ReferenceChangeEvent<String> event) {
                source.removeListener(this);
            }
        };
        ChangeListener<String, ChangeEvent<String>> changeListener2 = new ChangeListener<String, ChangeEvent<String>>() {
            @Override
            public void onChange(Observable<String, ? extends ChangeEvent<String>> source, ChangeEvent<String> event) {
                source.removeListener(this);
            }
        };

        ObservableReference<String, ReferenceChangeEvent<String>> observableReference = observableReferenceFactory.get();
        observableReference.addListener(changeListener1);
        observableReference.addListener(changeListener2);
    }

    @Test
    public void testChange() throws Exception {
        ChangeListener<String, ReferenceChangeEvent<String>> changeListenerMock = mock(ChangeListener.class);
        ObservableReference<String, ReferenceChangeEvent<String>> observableReference = observableReferenceFactory.get();

        observableReference.set("old ololo");
        observableReference.addListener(changeListenerMock);
        observableReference.set("new ololo");

        verify(changeListenerMock, timeout(100)).onChange(same(observableReference), argThat(Matchers.<ReferenceChangeEvent<String>>transformationMatcher()
                .with(namedTransformation("getOld", ReferenceChangeEvent<String>::getOld), new IsEqual<>("old ololo"))
                .with(namedTransformation("getNew", ReferenceChangeEvent<String>::getNew), new IsEqual<>("new ololo"))));
    }
}
