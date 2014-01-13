package com.myzone.reactive.stream;

import com.google.common.collect.Iterables;
import com.myzone.reactive.collection.ObservableIterable;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.reference.ObservableReadonlyReference;
import com.myzone.reactive.stream.collectors.ObservableCollectors;
import org.junit.Test;

import java.util.Optional;

import static com.myzone.reactive.stream.ObservableStreams.observableStreamOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author myzone
 * @date 03.01.14
 */
public class ObservableStreamTest {

    @Test
    public void testCollect() {
        ObservableIterable<Integer, ReferenceChangeEvent<Integer>> is = observableStreamOf(asList(1, 2, 3)).collect(ObservableCollectors
                .<Integer>toObservableIterable());

        assertTrue(Iterables.elementsEqual(is, asList(1, 2, 3)));
    }

    @Test
    public void testFilter() {
        ObservableIterable<Integer, ReferenceChangeEvent<Integer>> is = observableStreamOf(asList(1, 2, 3)).filter(integer -> integer % 2 != 0)
                .collect(ObservableCollectors.<Integer>toObservableIterable());

        assertTrue(Iterables.elementsEqual(is, asList(1, 3)));
    }

    @Test
    public void testMap() {
        ObservableIterable<Boolean, ReferenceChangeEvent<Boolean>> is = observableStreamOf(asList(1, 2, 3)).map(integer -> integer % 2 != 0)
                .collect(ObservableCollectors.<Boolean>toObservableIterable());

        assertTrue(Iterables.elementsEqual(is, asList(true, false, true)));
    }

    @Test
    public void testReduceEmpty() {
        ObservableReadonlyReference<Optional<Object>, ReferenceChangeEvent<Optional<Object>>> i = observableStreamOf(asList())
                .reduce((l, r) -> l.hashCode() > r.hashCode() ? l : r);

        assertFalse(i.get().isPresent());
    }

    @Test
    public void testReduceOne() {
        ObservableReadonlyReference<Optional<Integer>, ReferenceChangeEvent<Optional<Integer>>> i = observableStreamOf(asList(42))
                .reduce((l, r) -> l + r);

        assertEquals(42, (int) i.get().get());
    }

    @Test
    public void testReduce() {
        ObservableReadonlyReference<Optional<Integer>, ReferenceChangeEvent<Optional<Integer>>> i = observableStreamOf(asList(1, 2, 3))
                .reduce((l, r) -> l + r);

        assertEquals(6, (int) i.get().get());
    }


    @Test
    public void testMapAndFilter() {
        ObservableIterable<Integer, ReferenceChangeEvent<Integer>> is = observableStreamOf(asList(1, 2, 3)).map(integer -> integer + 1)
                .filter(integer -> integer % 2 == 0)
                .collect(ObservableCollectors.<Integer>toObservableIterable());

        assertTrue(Iterables.elementsEqual(is, asList(2, 4)));
    }

    @Test
    public void testMapFilterReduce() {
        ObservableReadonlyReference<Optional<Integer>, ReferenceChangeEvent<Optional<Integer>>> i = observableStreamOf(asList(1, 2, 3))
                .map(integer -> integer + 1)
                .filter(integer -> integer % 2 == 0)
                .reduce((l, r) -> l + r);

        assertEquals(6, (int) i.get().get());
    }

}
