package com.myzone.reactivestorage.accessor;

/**
 * @author myzone
 * @date 12.01.14
 */
public class InMemoryObservableDataAccessorTest extends ObservableDataAccessorTest {

    public InMemoryObservableDataAccessorTest() {
        super(InMemoryObservableDataAccessor::new);
    }

}
