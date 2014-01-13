package com.myzone.reactive.stream;

import com.google.common.collect.Iterators;
import com.myzone.annotations.NotNull;
import com.myzone.reactive.observable.Observables;

import java.util.Iterator;

/**
 * @author myzone
 * @date 03.01.14
 */
public final class ObservableStreams extends Observables {

    public static @NotNull <T> ObservableStream<T> observableStreamOf(Iterable<T> iterable) {
        return observableStreamOf(Iterators.<T>unmodifiableIterator(iterable.iterator()));
    }

    public static @NotNull <T> ObservableStream<T> observableStreamOf(Iterator<T> iterator) {
        return new AbstractObservableStream<T>() {
            public @Override @NotNull Iterator<T> iterator() {
                return iterator;
            }
        };
    }

}
