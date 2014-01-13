package com.myzone.reactivestorage.accessor;

import com.myzone.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface IndexedStorageDataAccessor<T> {

    @NotNull OptimizationManager<T> getOptimizationManager();

    interface OptimizationManager<T> {

        <V> Predicate<@NotNull T> byIndex(@NotNull Index<T, V> index, V value);

    }

    interface Index<T, V> {
        /* is just maker */
    }

}
