package com.myzone.reactive.stream;

import com.myzone.reactive.events.ChangeEvent;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.observable.Observable;
import com.myzone.reactive.reference.ObservableReadonlyReference;
import com.myzone.reactive.stream.collectors.ObservableCollector;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ObservableStream<T> {

    ObservableStream<T> filter(Predicate<? super T> filter);

    <R> ObservableStream<R> map(Function<? super T, R> mapper);

    ObservableReadonlyReference<Optional<T>, ReferenceChangeEvent<Optional<T>>> reduce(BiFunction<? super T, ? super T, ? extends T> reducer);

    <E extends ChangeEvent<T>, R extends Observable<T, E>> R collect(ObservableCollector<T, E, R> collector);

}
