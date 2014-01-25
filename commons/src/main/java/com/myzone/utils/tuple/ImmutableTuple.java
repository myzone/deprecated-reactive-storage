package com.myzone.utils.tuple;

import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;

/**
 * @author myzone
 * @date 9/8/13
 */
public @Immutable class ImmutableTuple<D, T extends Tuple> implements Tuple<D, T> {

    private final D data;
    private final @NotNull T next;

    public ImmutableTuple(D data, @NotNull T next) {
        this.data = data;
        this.next = next;
    }

    public @Override D get() {
        return data;
    }

    public @Override @NotNull T next() {
        return next;
    }

}