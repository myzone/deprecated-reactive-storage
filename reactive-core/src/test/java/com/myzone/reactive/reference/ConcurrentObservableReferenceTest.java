package com.myzone.reactive.reference;

/**
 * @author myzone
 * @date 11.01.14
 */
public class ConcurrentObservableReferenceTest extends ObservableReferenceTest {

    public ConcurrentObservableReferenceTest() {
        super(ConcurrentObservableReference<String>::new);
    }

}
