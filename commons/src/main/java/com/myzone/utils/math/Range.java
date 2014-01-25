package com.myzone.utils.math;

import com.google.common.collect.AbstractIterator;
import com.myzone.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author myzone
 * @date 25.01.14
 */
public interface Range<T> extends Iterable<Counter<T>> {

    @NotNull Counter<T> getStart();

    @NotNull Counter<T> getEnd();

    @NotNull Range<T> reverse();

    default @Override Iterator<Counter<T>> iterator() {
        Counter<T> end = getEnd();
        Counter<T> start = getStart();

        Predicate<Counter<T>> endBoundaryPredicate = start.compareTo(end) > 0
                                                     ? counter -> counter.compareTo(end) >= 0
                                                     : counter -> counter.compareTo(end) < 0;

        Function<Counter<T>, Counter<T>> getNextFunction = start.compareTo(end) > 0
                                                           ? Counter<T>::increment
                                                           : Counter<T>::decrement;

        return new AbstractIterator<Counter<T>>() {

            private @NotNull Counter<T> current = start;

            protected @Override Counter<T> computeNext() {
                if (endBoundaryPredicate.test(current)) {
                    return current = getNextFunction.apply(current);
                } else {
                    return endOfData();
                }
            }

        };
    }

}
