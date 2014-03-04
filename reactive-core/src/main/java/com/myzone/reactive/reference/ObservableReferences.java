package com.myzone.reactive.reference;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ImmutableReferenceChangeEvent;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.observable.AbstractObservable;
import com.myzone.reactive.observable.Observables;
import com.myzone.reactive.utils.DeadListenersCollector;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.myzone.reactive.observable.Observable.ChangeListener;

/**
 * @author myzone
 * @date 31.12.13
 */
public class ObservableReferences extends Observables {

    private static @NotNull DeadListenersCollector listenersCollector = getListenersCollector();

    public static @NotNull <T, K> ObservableReadonlyReference<K, ReferenceChangeEvent<K>> map(@NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> origin, @NotNull Function<T, K> mapper) {
        return new MappedObservableReference<>(origin, mapper);
    }

    public static @NotNull <T> ObservableReadonlyReference<T, ReferenceChangeEvent<T>> reduce(@NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> left, @NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> right, @NotNull BiFunction<T, T, T> reducer) {
        return new ReducedObservableReference<>(left, right, reducer);
    }

    public static @NotNull <T> Binding bind(@NotNull ObservableReference<T, ReferenceChangeEvent<T>> subscriber, @NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> publisher) {
        WeakReference<ObservableReference<T, ReferenceChangeEvent<T>>> weakSubscriber = new WeakReference<>(subscriber);
        ChangeListener<T, ReferenceChangeEvent<T>> changeListener = (source, event) -> {
            ObservableReference<T, ReferenceChangeEvent<T>> strongSubscriber = weakSubscriber.get();

            if (subscriber != null) {
                strongSubscriber.set(event.getNew());
            }
        };

        subscriber.set(publisher.get());
        publisher.addListener(changeListener);
        listenersCollector
                .collect(changeListener)
                .afterDeathOf(weakSubscriber)
                .via(publisher::removeListener);

        return () -> publisher.removeListener(changeListener);
    }

    public interface Binding {

        void dispose();

    }

    protected static class MappedObservableReference<S, T> extends AbstractObservable<T, ReferenceChangeEvent<T>> implements ObservableReadonlyReference<T, ReferenceChangeEvent<T>> {

        private final @NotNull ObservableReadonlyReference<S, ReferenceChangeEvent<S>> origin;
        private final @NotNull Function<S, T> mapper;

        private final @NotNull ChangeListener<S, ReferenceChangeEvent<S>> changeListener;

        public MappedObservableReference(@NotNull ObservableReadonlyReference<S, ReferenceChangeEvent<S>> origin, @NotNull Function<S, T> mapper) {
            this.origin = origin;
            this.mapper = mapper;

            WeakReference<MappedObservableReference<S, T>> weakSelf = new WeakReference<>(this);
            changeListener = (source, event) -> {
                T mappedOld = mapper.apply(event.getOld());
                T mappedNew = mapper.apply(event.getNew());

                if (!Objects.equals(mappedOld, mappedNew)) {
                    MappedObservableReference<S, T> strongSelf = weakSelf.get();

                    if (strongSelf != null) {
                        strongSelf.fireEvent(ImmutableReferenceChangeEvent.of(mappedOld, mappedNew));
                    }
                }
            };

            origin.addListener(changeListener);
        }

        protected @Override void finalize() throws Throwable {
            origin.removeListener(changeListener);

            super.finalize();
        }

        public @Override T get() {
            return mapper.apply(origin.get());
        }

    }

    protected static class ReducedObservableReference<T> extends AbstractObservable<T, ReferenceChangeEvent<T>> implements ObservableReadonlyReference<T, ReferenceChangeEvent<T>> {

        private final @NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> left;
        private final @NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> right;
        private final @NotNull BiFunction<T, T, T> reducer;

        private final @NotNull WeakReference<ReducedObservableReference<T>> weakSelf;
        private final @NotNull ChangeListener<T, ReferenceChangeEvent<T>> leftChangeListener;
        private final @NotNull ChangeListener<T, ReferenceChangeEvent<T>> rightChangeListener;

        public ReducedObservableReference(@NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> left, @NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> right, @NotNull BiFunction<T, T, T> reducer) {
            this.left = left;
            this.right = right;
            this.reducer = reducer;

            weakSelf = new WeakReference<>(this);

            leftChangeListener = (source, event) -> {
                T reducedOld = reducer.apply(event.getOld(), right.get());
                T reducedNew = reducer.apply(event.getNew(), right.get());

                onChange(reducedOld, reducedNew);
            };

            rightChangeListener = (source, event) -> {
                T reducedOld = reducer.apply(left.get(), event.getOld());
                T reducedNew = reducer.apply(left.get(), event.getNew());

                onChange(reducedOld, reducedNew);
            };

            left.addListener(leftChangeListener);
            right.addListener(rightChangeListener);
        }

        protected @Override void finalize() throws Throwable {
            left.removeListener(leftChangeListener);
            right.removeListener(rightChangeListener);

            super.finalize();
        }

        public @Override T get() {
            return reducer.apply(left.get(), right.get());
        }

        private void onChange(T reducedOld, T reducedNew) {
            if (!Objects.equals(reducedOld, reducedNew)) {
                ReducedObservableReference<T> strongSelf = weakSelf.get();

                if (strongSelf != null) {
                    strongSelf.fireEvent(ImmutableReferenceChangeEvent.of(reducedOld, reducedNew));
                }
            }
        }

    }

}
