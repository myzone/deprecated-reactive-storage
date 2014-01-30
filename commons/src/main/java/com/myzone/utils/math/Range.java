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

        Function<Counter<T>, Counter<T>> nextFunction = start.compareTo(end) < 0
                                                           ? Counter<T>::increment
                                                           : Counter<T>::decrement;

        return new AbstractIterator<Counter<T>>() {

            private @NotNull Counter<T> next = start;

            protected @Override Counter<T> computeNext() {
                Counter<T> result = next;
                if (endBoundaryPredicate.test(result)) {
                    next = nextFunction.apply(result);

                    return result;
                } else {
                    return endOfData();
                }
            }

        };
    }

}
