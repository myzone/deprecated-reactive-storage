package com.myzone.reactive.stream;

import com.google.common.collect.AbstractIterator;
import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ChangeEvent;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.observable.Observable;
import com.myzone.reactive.observable.ObservableHelper;
import com.myzone.reactive.observable.Observables;
import com.myzone.reactive.reference.ConcurrentObservableReference;
import com.myzone.reactive.reference.ObservableReadonlyReference;
import com.myzone.reactive.stream.collectors.ObservableCollector;
import com.myzone.reactive.utils.DeadListenersCollector;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.*;

import static com.myzone.reactive.observable.Observable.ChangeListener;

/**
 * @author myzone
 * @date 30.12.13
 */
public abstract class AbstractObservableStream<T> implements ObservableStream<T>, Iterable<T> {

    private static @NotNull DeadListenersCollector listenersCollector = Observables.getListenersCollector();

    private final @NotNull ObservableHelper<T, ReferenceChangeEvent<T>> observableHelper;

    protected AbstractObservableStream() {
        observableHelper = new ObservableHelper<>();
    }

    public @Override @NotNull ObservableStream<T> filter(Predicate<? super T> filter) {
        return new FilteredObservableStream<>(this, filter);
    }

    public @Override @NotNull <R> ObservableStream<R> map(Function<? super T, R> mapper) {
        return new MappedObservableStream<>(this, mapper);
    }


    public @Override @NotNull ObservableReadonlyReference<Optional<T>, ReferenceChangeEvent<Optional<T>>> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        ConcurrentObservableReference<Optional<T>> observableReference = new ConcurrentObservableReference<>(doReduce(reducer));

        WeakReference<ConcurrentObservableReference<Optional<T>>> weakObservableReference = new WeakReference<>(observableReference);
        ChangeListener<T, ReferenceChangeEvent<T>> changeListener = (source, event) -> {
            ConcurrentObservableReference<Optional<T>> strongObservableReference = weakObservableReference.get();

            if (strongObservableReference != null) {
                strongObservableReference.set(doReduce(reducer));
            }
        };

        observableHelper.addListener(changeListener);
        listenersCollector.collect(changeListener)
                .afterDeathOf(weakObservableReference)
                .via(observableHelper::removeListener);

        return observableReference;
    }

    public @Override @NotNull <E extends ChangeEvent<T>, R extends Observable<T, E>> R collect(ObservableCollector<T, E, R> collector) {
        Consumer<T> consumer = collector.createConsumer();
        Iterator<T> iterator = iterator();

        while (true) {
            if (iterator.hasNext()) {
                consumer.accept(iterator.next());
            } else {
                ObservableCollector.Summary<T, R> summary = collector.summarize(consumer);

                R result = summary.getResult();
                BiConsumer<T, T> onChangeListener = summary.getOnChangeListener();

                ChangeListener<T, ReferenceChangeEvent<T>> changeListener = (source, event) -> {
                    onChangeListener.accept(event.getOld(), event.getNew());
                };

                observableHelper.addListener(changeListener);
                listenersCollector.collect(changeListener)
                        .afterDeathOf(new WeakReference<R>(result))
                        .via(observableHelper::removeListener);

                return summary.getResult();
            }
        }
    }

    protected @NotNull Optional<T> doReduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        Iterator<T> iterator = iterator();

        if (iterator.hasNext()) {
            T result = iterator.next();

            while (iterator.hasNext()) {
                result = reducer.apply(result, iterator.next());
            }

            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    protected static class FilteredObservableStream<T> extends AbstractObservableStream<T> {

        protected final @NotNull AbstractObservableStream<T> origin;
        protected final @NotNull Predicate<? super T> predicate;

        public FilteredObservableStream(@NotNull AbstractObservableStream<T> origin, @NotNull Predicate<? super T> predicate) {
            this.origin = origin;
            this.predicate = predicate;
        }

        public @Override @NotNull Iterator<T> iterator() {
            Iterator<T> originIterator = origin.iterator();

            return new AbstractIterator<T>() {
                protected @Override T computeNext() {
                    while (originIterator.hasNext()) {
                        T next = originIterator.next();

                        if (predicate.test(next))
                            return next;
                    }

                    return endOfData();
                }
            };
        }

    }

    protected static class MappedObservableStream<S, T> extends AbstractObservableStream<T> {

        protected final @NotNull AbstractObservableStream<S> origin;
        protected final @NotNull Function<? super S, T> function;

        public MappedObservableStream(@NotNull AbstractObservableStream<S> origin, @NotNull Function<? super S, T> function) {
            this.origin = origin;
            this.function = function;
        }

        public @Override @NotNull Iterator<T> iterator() {
            Iterator<S> originIterator = origin.iterator();

            return new Iterator<T>() {
                public @Override boolean hasNext() {
                    return originIterator.hasNext();
                }

                public @Override T next() {
                    return function.apply(originIterator.next());
                }
            };
        }

    }

}
