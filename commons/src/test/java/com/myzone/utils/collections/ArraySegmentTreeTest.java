package com.myzone.utils.collections;

/**
 * @author myzone
 * @date 30.01.14.
 */
public class ArraySegmentTreeTest extends SegmentTreeTest {

    public ArraySegmentTreeTest() {
        super((m, d) -> new ArraySegmentTree<Integer>(m, d));
    }

}
