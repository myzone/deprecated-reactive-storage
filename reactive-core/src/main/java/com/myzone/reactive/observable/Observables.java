package com.myzone.reactive.observable;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ChangeEvent;
import com.myzone.reactive.utils.DeadListenersCollector;
import com.myzone.utils.UtilityClass;

import java.lang.ref.WeakReference;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author myzone
 * @date 03.01.14
 */
public class Observables extends UtilityClass {

    private static final @NotNull DeadListenersCollector LISTENERS_COLLECTOR = new DeadListenersCollector();

    /**
     * DeadListenersCollector is a quite expensive, because of its new thread for processing dead listeners creation.
     * So this method is a way to provide cached one.
     * But you should keep in mind that statics are failure-point of testability and this method should be used carefully.
     *
     * @return cached instance of DeadListenersCollector
     */
    public static @NotNull DeadListenersCollector getListenersCollector() {
        return LISTENERS_COLLECTOR;
    }

    public static @NotNull <T, E extends ChangeEvent<T>> ObservableHelper<T, E> newObservableHelper() {
        return new SimpleObservableHelper<>();
    }

    public static @NotNull <T, E extends ChangeEvent<T>> FilteredObservable<T, E> filter(@NotNull Observable<T, E> origin, @NotNull Predicate<E> predicate) {
        return new FilteredObservable<>(origin, predicate);
    }

    public static @NotNull <T, E extends ChangeEvent<T>> FilteredObservableHelper<T, E> filter(@NotNull ObservableHelper<T, E> origin, @NotNull Predicate<E> predicate) {
        return new FilteredObservableHelper<>(origin, predicate);
    }

    public static @NotNull <S, T, ES extends ChangeEvent<S>, ET extends ChangeEvent<T>> Observable<T, ET> map(@NotNull Observable<S, ES> origin, @NotNull Function<ES, ET> mapper) {
        return new MappedObservable<>(origin, mapper);
    }

    public static @NotNull <S, T, ES extends ChangeEvent<S>, ET extends ChangeEvent<T>> ObservableHelper<T, ET> map(@NotNull ObservableHelper<S, ES> origin, @NotNull Function<ES, ET> mapper) {
        return new MappedObservableHelper<>(origin, mapper);
    }


    protected static class FilteredObservable<T, E extends ChangeEvent<T>> extends AbstractObservable<T, E> {

        private final @NotNull Observable<T, E> origin;
        private final @NotNull ChangeListener<T, E> changeListener;

        public FilteredObservable(Observable<T, E> origin, @NotNull Predicate<E> predicate) {
            this.origin = origin;

            WeakReference<FilteredObservable<T, E>> weakSelf = new WeakReference<>(this);
            changeListener = (source, event) -> {
                FilteredObservable<T, E> strongSelf = weakSelf.get();

                if (strongSelf != null) {
                    if (predicate.test(event)) {
                        super.fireEvent(event);
                    }
                }
            };

            origin.addListener(changeListener);
        }

        protected @Override void finalize() throws Throwable {
            origin.removeListener(changeListener);

            super.finalize();
        }

    }

    protected static class MappedObservable<S, T, ES extends ChangeEvent<S>, ET extends ChangeEvent<T>> extends AbstractObservable<T, ET> {

        private final @NotNull Observable<S, ES> origin;
        private final @NotNull ChangeListener<S, ES> changeListener;

        public MappedObservable(@NotNull Observable<S, ES> origin, @NotNull Function<ES, ET> mapper) {
            this.origin = origin;

            WeakReference<MappedObservable<S, T, ES, ET>> weakSelf = new WeakReference<>(this);
            changeListener = (source, event) -> {
                MappedObservable<S, T, ES, ET> strongSelf = weakSelf.get();

                if (strongSelf != null) {
                    strongSelf.fireEvent(mapper.apply(event));
                }
            };

            origin.addListener(changeListener);
        }

        protected @Override void finalize() throws Throwable {
            origin.removeListener(changeListener);

            super.finalize();
        }

    }

    protected static class SimpleObservableHelper<T, E extends ChangeEvent<T>> extends AbstractObservable<T, E> implements ObservableHelper<T, E> {

        public @Override void fireEvent(@NotNull E changeEvent) {
            super.fireEvent(changeEvent);
        }

    }

    protected static class FilteredObservableHelper<T, E extends ChangeEvent<T>> extends FilteredObservable<T, E> implements ObservableHelper<T, E> {

        public FilteredObservableHelper(ObservableHelper<T, E> origin, @NotNull Predicate<E> predicate) {
            super(origin, predicate);
        }

        public @Override void fireEvent(@NotNull E changeEvent) {
            super.fireEvent(changeEvent);
        }

    }

    protected static class MappedObservableHelper<S, T, ES extends ChangeEvent<S>, ET extends ChangeEvent<T>> extends MappedObservable<S, T, ES, ET> implements ObservableHelper<T, ET> {

        public MappedObservableHelper(@NotNull Observable<S, ES> origin, @NotNull Function<ES, ET> mapper) {
            super(origin, mapper);
        }

        public @Override void fireEvent(@NotNull ET changeEvent) {
            super.fireEvent(changeEvent);
        }

    }


}
