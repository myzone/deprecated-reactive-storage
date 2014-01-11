package com.myzone.reactive.stream;

import com.myzone.reactive.observable.Observables;

import java.util.Iterator;

/**
 * @author myzone
 * @date 03.01.14
 */
public final class ObservableStreams extends Observables {

    public static <T> ObservableStream<T> observableStreamOf(Iterable<T> iterable) {
        return observableStreamOf(iterable.iterator());
    }

    public static <T> ObservableStream<T> observableStreamOf(Iterator<T> iterator) {
        return new AbstractObservableStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }

}
