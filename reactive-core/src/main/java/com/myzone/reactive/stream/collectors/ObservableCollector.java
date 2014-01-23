package com.myzone.reactive.stream.collectors;

import com.google.common.base.Objects;
import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ChangeEvent;
import com.myzone.reactive.observable.Observable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ObservableCollector<T, E extends ChangeEvent<T>, R extends Observable<T, E>> {

    @NotNull Consumer<T> createConsumer();

    @NotNull Summary<T, R> summarize(Consumer<T> consumer);

    final @Immutable class Summary<T, R> {

        private final @NotNull R result;

        private final @NotNull BiConsumer<T, T> onChangeListener;

        public Summary(@NotNull R result, @NotNull BiConsumer<T, T> onChangeListener) {
            this.result = result;
            this.onChangeListener = onChangeListener;
        }

        public @NotNull R getResult() {
            return result;
        }

        public @NotNull BiConsumer<T, T> getOnChangeListener() {
            return onChangeListener;
        }

        public @Override boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Summary summary = (Summary) o;

            if (!onChangeListener.equals(summary.onChangeListener))
                return false;
            if (!result.equals(summary.result))
                return false;

            return true;
        }

        public @Override int hashCode() {
            int result1 = result.hashCode();
            result1 = 31 * result1 + onChangeListener.hashCode();
            return result1;
        }

        public @Override String toString() {
            return Objects.toStringHelper(this)
                    .add("result", result)
                    .add("onChangeListener", onChangeListener)
                    .toString();
        }
    }

}
