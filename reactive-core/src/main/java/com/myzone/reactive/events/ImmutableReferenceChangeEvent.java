package com.myzone.reactive.events;

import com.google.common.base.Objects;
import com.myzone.annotations.Immutable;

/**
 * @author myzone
 * @date 13.01.14.
 */
public @Immutable class ImmutableReferenceChangeEvent<T> implements ReferenceChangeEvent<T> {

    private final T oldValue;
    private final T newValue;

    private ImmutableReferenceChangeEvent(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public @Override T getOld() {
        return oldValue;
    }

    public @Override T getNew() {
        return newValue;
    }

    public @Override boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ImmutableReferenceChangeEvent))
            return false;

        ImmutableReferenceChangeEvent that = (ImmutableReferenceChangeEvent) o;

        if (newValue != null ? !newValue.equals(that.newValue) : that.newValue != null)
            return false;
        if (oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null)
            return false;

        return true;
    }

    public @Override int hashCode() {
        int result = oldValue != null ? oldValue.hashCode() : 0;
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }

    public @Override String toString() {
        return Objects.toStringHelper(this)
                .add("oldValue", oldValue)
                .add("newValue", newValue)
                .toString();
    }

    public static <T> ImmutableReferenceChangeEvent<T> of(T oldValue, T newValue) {
        return new ImmutableReferenceChangeEvent<T>(oldValue, newValue);
    }

}
