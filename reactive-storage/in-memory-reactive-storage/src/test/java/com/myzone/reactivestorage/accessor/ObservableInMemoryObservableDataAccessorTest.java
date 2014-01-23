package com.myzone.reactivestorage.accessor;

/**
 * @author myzone
 * @date 12.01.14
 */
public class ObservableInMemoryObservableDataAccessorTest extends ObservableDataAccessorTest {

    public ObservableInMemoryObservableDataAccessorTest() {
        super(ObservableInMemoryDataAccessor::new);
    }

}
