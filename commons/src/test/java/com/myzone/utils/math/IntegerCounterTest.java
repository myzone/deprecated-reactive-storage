package com.myzone.utils.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author myzone
 * @date 25.01.14
 */
public class IntegerCounterTest {

    @Test
    public void testIncrement() throws Exception {
        assertEquals(IntegerCounter.of(0), IntegerCounter.of(-1).increment());
    }

    @Test
    public void testDecrement() throws Exception {
        assertEquals(IntegerCounter.of(0), IntegerCounter.of(1).decrement());
    }

    @Test
    public void testCompare() throws Exception {
        assertTrue(IntegerCounter.of(0).compareTo(IntegerCounter.of(0)) == 0);
        assertTrue(IntegerCounter.of(0).compareTo(IntegerCounter.of(1)) < 0);
        assertTrue(IntegerCounter.of(0).compareTo(IntegerCounter.of(-1)) > 0);
    }

}
