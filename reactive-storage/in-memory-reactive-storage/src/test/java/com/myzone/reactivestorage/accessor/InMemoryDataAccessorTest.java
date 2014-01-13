package com.myzone.reactivestorage.accessor;

import myzone.reactivestorage.accessor.DataAccessorTest;

/**
 * @author myzone
 * @date 12.01.14
 */
public class InMemoryDataAccessorTest extends DataAccessorTest {

    public InMemoryDataAccessorTest() {
        super(InMemoryDataAccessor::new);
    }

}
