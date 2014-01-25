package com.myzone.utils.math;

import com.myzone.annotations.NotNull;

/**
 * @author myzone
 * @date 25.01.14
 */
public interface Counter<T> extends Comparable<Counter<T>> {

    @NotNull Counter<T> increment();

    @NotNull Counter<T> decrement();

    @NotNull Range<T> to(@NotNull Counter<T> counter);

    @NotNull Range<T> from(@NotNull Counter<T> counter);

    @NotNull T get();

}
