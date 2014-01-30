package com.myzone.utils.collections;

import com.myzone.utils.funcional.Monoid;
import com.myzone.utils.math.IntegerCounter;
import org.junit.Test;

import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

/**
 * @author myzone
 * @date 30.01.14.
 */
public abstract class SegmentTreeTest {

    private final BiFunction<Monoid<Integer>, Integer[], SegmentTree<Integer, Integer>> treeSupplier;

    protected SegmentTreeTest(BiFunction<Monoid<Integer>, Integer[], SegmentTree<Integer, Integer>> treeSupplier) {
        this.treeSupplier = treeSupplier;
    }

    @Test
    public void testAggregate() throws Exception {
        SegmentTree<Integer, Integer> tree = treeSupplier.apply(new IntegerSumMonoid(), new Integer[]{1, 2, 3, 4, 5});

        assertEquals(1, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(1))));
        assertEquals(6, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(3))));
        assertEquals(15, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(5))));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testArrayIndexOutOfBoundsException() throws Exception {
        SegmentTree<Integer, Integer> tree = treeSupplier.apply(new IntegerSumMonoid(), new Integer[]{1, 2, 3, 4, 5});

        assertEquals(15, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(6))));
    }

    @Test
    public void testUpdate() throws Exception {
        SegmentTree<Integer, Integer> tree = treeSupplier.apply(new IntegerSumMonoid(), new Integer[]{1, 2, 3, 4, 5});

        assertEquals(1, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(1))));
        assertEquals(6, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(3))));
        assertEquals(15, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(5))));

        tree.update(IntegerCounter.of(0).to(IntegerCounter.of(3)), i -> i + 1);

        assertEquals(1 + 1, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(1))));
        assertEquals(6 + 3, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(3))));
        assertEquals(15 + 3, (int) tree.aggregate(IntegerCounter.of(0).to(IntegerCounter.of(5))));
    }

    private static class IntegerSumMonoid implements Monoid<Integer> {

        public @Override Integer getNeutral() {
            return 0;
        }

        public @Override BiFunction<Integer, Integer, Integer> getFunction() {
            return (a, b) -> a + b;
        }

    }

}
