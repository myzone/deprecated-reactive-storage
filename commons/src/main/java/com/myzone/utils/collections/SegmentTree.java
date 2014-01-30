package com.myzone.utils.collections;

import com.myzone.annotations.NotNull;
import com.myzone.utils.math.Range;

import java.util.function.Function;

/**
 * @author myzone
 * @date 30.01.14
 */
public interface SegmentTree<T, I> {

    T aggregate(@NotNull Range<I> range);

    void update(@NotNull Range<I> range, @NotNull Function<T, T> updater);

}
