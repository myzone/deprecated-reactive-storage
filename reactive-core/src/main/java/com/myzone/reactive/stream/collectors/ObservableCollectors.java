package com.myzone.reactive.stream.collectors;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.collection.ObservableIterable;
import com.myzone.reactive.events.ImmutableReferenceChangeEvent;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.utils.UtilityClass;

import java.util.*;
import java.util.function.Consumer;

import static com.myzone.reactive.observable.Observable.ChangeListener;

/**
 * @author myzone
 * @date 30.12.13
 */
public final class ObservableCollectors extends UtilityClass {

    public static <T> ObservableCollector<T, ReferenceChangeEvent<T>, ObservableIterable<T, ReferenceChangeEvent<T>>> toObservableIterable() {
        Map<Consumer<T>, Iterable<T>> consumersMap = new LinkedHashMap<>();

        return new ObservableCollector<T, ReferenceChangeEvent<T>, ObservableIterable<T, ReferenceChangeEvent<T>>>() {
            public @Override @NotNull Consumer<T> createConsumer() {
                List<T> iterable = new ArrayList<>();
                Consumer<T> consumer = iterable::add;

                consumersMap.put(consumer, iterable);

                return consumer;
            }

            public @Override @NotNull Summary<T, ObservableIterable<T, ReferenceChangeEvent<T>>> summarize(Consumer<T> consumer) {
                Iterable<T> iterable = consumersMap.remove(consumer);
                List<ChangeListener<T, ? super ReferenceChangeEvent<T>>> changeListeners = new ArrayList<>();

                ObservableIterable<T, ReferenceChangeEvent<T>> result = new ObservableIterable<T, ReferenceChangeEvent<T>>() {
                    public @Override Iterator<T> iterator() {
                        return iterable.iterator();
                    }

                    public @Override void addListener(ChangeListener<T, ? super ReferenceChangeEvent<T>> changeListener) {
                        changeListeners.add(changeListener);
                    }

                    public @Override void removeListener(ChangeListener<T, ? super ReferenceChangeEvent<T>> changeListener) {
                        changeListeners.remove(changeListener);
                    }

                    public @Override boolean equals(Object obj) {
                        return iterable.equals(obj);
                    }

                    public @Override int hashCode() {
                        return iterable.hashCode();
                    }

                    public @Override String toString() {
                        return iterable.toString();
                    }
                };

                return new Summary<>(result, (oldValue, newValue) -> {
                    ReferenceChangeEvent<T> changeEvent = ImmutableReferenceChangeEvent.of(oldValue, newValue);

                    changeListeners.forEach(listener -> {
                        listener.onChange(result, changeEvent);
                    });
                });
            }
        };
    }
}
