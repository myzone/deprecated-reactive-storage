package com.myzone.utils.math;

import com.google.common.base.Objects;
import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;

import static java.lang.Math.addExact;
import static java.lang.Math.subtractExact;

/**
 * @author myzone
 * @date 25.01.14
 */
public @Immutable class IntegerCounter implements Counter<Integer> {

    private final @NotNull Integer value;

    private IntegerCounter(@NotNull Integer value) {
        this.value = value;
    }

    public @Override @NotNull Counter<Integer> increment() {
        return of(addExact(value, 1));
    }

    public @Override @NotNull Counter<Integer> decrement() {
        return of(subtractExact(value, 1));
    }

    public @Override @NotNull Range<Integer> to(@NotNull Counter<Integer> counter) {
        return ImmutableRange.of(this, counter);
    }

    public @Override @NotNull Range<Integer> from(@NotNull Counter<Integer> counter) {
        return ImmutableRange.of(counter, this);
    }

    public @Override @NotNull Integer get() {
        return value;
    }

    public @Override int compareTo(Counter<Integer> o) {
        return value.compareTo(o.get());
    }

    public @Override boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerCounter)) return false;

        IntegerCounter that = (IntegerCounter) o;

        return value.equals(that.value);
    }

    public @Override int hashCode() {
        return value.hashCode();
    }

    public @Override String toString() {
        return Objects.toStringHelper(this)
                .add("value", value)
                .toString();
    }

    public static @NotNull IntegerCounter of(@NotNull Integer value) {
        return new IntegerCounter(value);
    }

}
