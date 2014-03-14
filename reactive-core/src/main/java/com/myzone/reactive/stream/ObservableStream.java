package com.myzone.reactive.stream;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.event.ChangeEvent;
import com.myzone.reactive.event.ReferenceChangeEvent;
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

    @NotNull ObservableStream<T> filter(@NotNull Predicate<? super T> filter);

    @NotNull <R> ObservableStream<R> map(@NotNull Function<? super T, R> mapper);

    @NotNull ObservableReadonlyReference<Optional<T>, ReferenceChangeEvent<Optional<T>>> reduce(@NotNull BiFunction<? super T, ? super T, ? extends T> reducer);

    @NotNull <E extends ChangeEvent<T>, R extends Observable<T, E>> R collect(@NotNull ObservableCollector<T, E, R> collector);

}
