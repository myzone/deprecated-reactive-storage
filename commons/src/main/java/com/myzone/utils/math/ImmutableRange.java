package com.myzone.utils.math;

import com.google.common.base.Objects;
import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;

/**
 * @author myzone
 * @date 25.01.14
 */
public @Immutable class ImmutableRange<T> implements Range<T> {

    private final @NotNull Counter<T> start;
    private final @NotNull Counter<T> end;

    private ImmutableRange(Counter<T> start, Counter<T> end) {
        this.start = start;
        this.end = end;
    }

    public @Override @NotNull Counter<T> getStart() {
        return start;
    }

    public @Override @NotNull Counter<T> getEnd() {
        return end;
    }

    public @Override @NotNull Range<T> reverse() {
        return of(end, start);
    }

    public @Override boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableRange)) return false;

        ImmutableRange that = (ImmutableRange) o;

        if (!end.equals(that.end)) return false;
        if (!start.equals(that.start)) return false;

        return true;
    }

    public @Override int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    public @Override String toString() {
        return Objects.toStringHelper(this)
                .add("start", start)
                .add("end", end)
                .toString();
    }

    public static @NotNull <T> ImmutableRange<T> of(Counter<T> start, Counter<T> end) {
        return new ImmutableRange<>(start, end);
    }

}

