package com.myzone.reactivestorage.accessor;

import java.util.function.Predicate;

/**
 * Created by myzone on 30.12.13.
 */
public interface IndexedStorageDataAccessor<T> {

    OptimizationManager<T> getOptimizationManager();

    interface OptimizationManager<T> {

        <V> Predicate<T> byIndex(Index<T, V> index, V value);

    }

    interface Index<T, V> {
        /* is just maker */
    }

}
