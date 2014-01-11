package com.myzone.reactive.reference;

import org.testng.annotations.Test;

/**
 * @author myzone
 * @date 11.01.14
 */
@Test
public class ConcurrentObservableReferenceTest extends ObservableReferenceTest {

    public ConcurrentObservableReferenceTest() {
        super(ConcurrentObservableReference<String>::new);
    }

}
