package com.myzone.reactivestorage.accessor;

/**
 * @author myzone
 * @date 26.01.14
 */
public class InMemoryObservableDataAccessorTest2 extends ObservableDataAccessorTest2 {

    public InMemoryObservableDataAccessorTest2() {
        super(InMemoryObservableDataAccessor::new);
    }
}
