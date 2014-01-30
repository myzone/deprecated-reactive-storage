package com.myzone.utils.collections;

import com.myzone.annotations.NotNull;
import com.myzone.utils.funcional.Monoid;
import com.myzone.utils.math.Range;

import java.util.function.Function;

/**
 * @author myzone
 * @date 30.01.14
 */
public class ArraySegmentTree<T> implements SegmentTree<T, Integer> {

    private final Monoid<T> monoid;
    private final int size;
    private final T[] data;

    @SuppressWarnings("unchecked")
    public ArraySegmentTree(Monoid<T> monoid, T[] data) {
        this.monoid = monoid;
        this.size = data.length;
        this.data = (T[]) new Object[upperPowerOfTwo(size) * 2];

        System.arraycopy(data, 0, this.data, this.data.length / 2, data.length);
        for (int i = data.length; i < this.data.length / 2; i++) {
            this.data[this.data.length / 2 + i] = monoid.getNeutral();
        }

        validate();
    }

    public @Override T aggregate(@NotNull Range<Integer> range) {
        range = normalize(range);

        if (!check(range))
            throw new ArrayIndexOutOfBoundsException(range.toString());

        int from = range.getStart().get() + data.length / 2;
        int to = range.getEnd().get() + data.length / 2 - 1;

        T result = monoid.getNeutral();

        while (from <= to) {
            if ((from & 1) == 1) {
                result = monoid.getFunction().apply(result, data[from]);
            }

            if ((to & 1) == 0) {
                result = monoid.getFunction().apply(result, data[to]);
            }

            from = (from + 1) / 2;
            to = (to - 1) / 2;
        }

        return result;
    }

    public @Override void update(@NotNull Range<Integer> range, @NotNull Function<T, T> updater) {
        range.forEach(counter -> {
            int index = counter.get() + data.length / 2;

            data[index] = updater.apply(data[index]);
        });

        validate();
    }

    protected void validate() {
        for (int i = (data.length / 2) - 1; i > 0; --i) {
            data[i] = monoid.getFunction().apply(data[i * 2], data[i * 2 + 1]);
        }
    }

    protected @NotNull Range<Integer> normalize(@NotNull Range<Integer> range) {
        if (range.getStart().compareTo(range.getEnd()) > 0)
            return range.reverse();

        return range;
    }

    protected boolean check(@NotNull Range<Integer> range) {
        return range.getStart().get() >= 0
                && range.getEnd().get() <= size;
    }

    private static int upperPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v++;

        return v;
    }

}
