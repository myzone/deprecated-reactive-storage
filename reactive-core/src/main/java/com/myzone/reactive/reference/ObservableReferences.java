package com.myzone.reactive.reference;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.observable.AbstractObservable;
import com.myzone.reactive.observable.Observable;
import com.myzone.reactive.observable.Observables;
import com.myzone.reactive.utils.DeadListenersCollector;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author myzone
 * @date 31.12.13
 */
public class ObservableReferences extends Observables {

    private static @NotNull DeadListenersCollector listenersCollector = getListenersCollector();

    public static @NotNull <T, K> ObservableReadonlyReference<K, ReferenceChangeEvent<K>> map(@NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> origin, @NotNull Function<T, K> mapper) {
        return new MappedObservableReference<>(origin, mapper);
    }

    public static <T> void bind(@NotNull ObservableReference<T, ReferenceChangeEvent<T>> subscriber, @NotNull ObservableReadonlyReference<T, ReferenceChangeEvent<T>> publisher) {
        WeakReference<ObservableReference<T, ReferenceChangeEvent<T>>> weakSubscriber = new WeakReference<>(subscriber);
        Observable.ChangeListener<T, ReferenceChangeEvent<T>> changeListener = (source, event) -> {
            ObservableReference<T, ReferenceChangeEvent<T>> strongSubscriber = weakSubscriber.get();

            if (subscriber != null) {
                strongSubscriber.set(event.getNew());
            }
        };

        subscriber.set(publisher.get());

        subscriber.addListener(changeListener);
        listenersCollector.collect(changeListener).afterDeathOf(weakSubscriber).via(publisher::removeListener);
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
                        strongSelf.fireEvent(new ReferenceChangeEvent<T>() {
                            public @Override T getOld() {
                                return mappedOld;
                            }

                            public @Override T getNew() {
                                return mappedNew;
                            }
                        });
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
}
