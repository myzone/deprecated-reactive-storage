package com.myzone.reactive.stream;

import com.google.common.base.Objects;
import com.google.common.collect.AbstractIterator;
import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ChangeEvent;
import com.myzone.reactive.events.ImmutableReferenceChangeEvent;
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

    private static final @NotNull DeadListenersCollector LISTENERS_COLLECTOR = Observables.getListenersCollector();

    private final @NotNull ObservableHelper<T, ReferenceChangeEvent<T>> observableHelper;

    protected AbstractObservableStream() {
        this(Observables.newObservableHelper());
    }

    protected AbstractObservableStream(@NotNull ObservableHelper<T, ReferenceChangeEvent<T>> observableHelper) {
        this.observableHelper = observableHelper;
    }

    public @Override @NotNull ObservableStream<T> filter(Predicate<? super T> filter) {
        return new FilteredObservableStream<>(observableHelper, this, filter);
    }

    public @Override @NotNull <R> ObservableStream<R> map(Function<? super T, R> mapper) {
        return new MappedObservableStream<>(observableHelper, this, mapper);
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
        LISTENERS_COLLECTOR.collect(changeListener)
                .afterDeathOf(weakObservableReference)
                .via(observableHelper::removeListener);

        return observableReference;
    }

    public @Override @NotNull <E extends ChangeEvent<T>, R extends Observable<T, E>> R collect(ObservableCollector<T, E, R> collector) {
        Consumer<T> consumer = collector.createConsumer();
        Iterator<T> iterator = iterator();

        iterator.forEachRemaining(consumer::accept);

        ObservableCollector.Summary<T, R> summary = collector.summarize(consumer);

        R result = summary.getResult();
        BiConsumer<T, T> onChangeListener = summary.getOnChangeListener();

        ChangeListener<T, ReferenceChangeEvent<T>> changeListener = (source, event) -> {
            onChangeListener.accept(event.getOld(), event.getNew());
        };

        observableHelper.addListener(changeListener);
        LISTENERS_COLLECTOR.collect(changeListener)
                .afterDeathOf(new WeakReference<R>(result))
                .via(observableHelper::removeListener);

        return summary.getResult();
    }

    @Override public String toString() {
        return Objects.toStringHelper(this).add("observableHelper", observableHelper).toString();
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

    protected void fireChanges(@NotNull ReferenceChangeEvent<T> changeEvent) {
        observableHelper.fireEvent(changeEvent);
    }

    protected static class FilteredObservableStream<T> extends AbstractObservableStream<T> {

        protected static @NotNull <T> Predicate<T> notNull() {
            return o -> o != null;
        }

        protected final @NotNull AbstractObservableStream<T> origin;
        protected final @NotNull Predicate<? super T> predicate;

        public FilteredObservableStream(@NotNull ObservableHelper<T, ReferenceChangeEvent<T>> observableHelper, @NotNull AbstractObservableStream<T> origin, @NotNull Predicate<? super T> predicate) {
            super(Observables.filter(observableHelper, changeEvent -> FilteredObservableStream.<T>notNull()
                    .and(predicate)
                    .test(changeEvent.getNew()) || FilteredObservableStream.<T>notNull()
                    .and(predicate)
                    .test(changeEvent.getOld())));

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
        protected final @NotNull Function<? super S, ? extends T> function;

        public MappedObservableStream(@NotNull ObservableHelper<S, ReferenceChangeEvent<S>> observableHelper, @NotNull AbstractObservableStream<S> origin, @NotNull Function<? super S, ? extends T> function) {
            super(Observables.map(observableHelper, originEvent -> new ImmutableReferenceChangeEvent<>(originEvent.getOld() != null
                                                                                                       ? function.apply(originEvent
                    .getOld())
                                                                                                       : null,
                    originEvent.getNew() != null
                    ? function.apply(originEvent.getNew())
                    : null)));

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
