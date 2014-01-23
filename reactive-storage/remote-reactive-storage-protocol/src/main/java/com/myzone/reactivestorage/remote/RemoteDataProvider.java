package com.myzone.reactivestorage.remote;

import com.myzone.annotations.NotNull;

/**
 * @author myzone
 * @date 15.01.14
 */
public interface RemoteDataProvider {

    @NotNull <T> Iterable<@NotNull Opportunity<T>> getAll(@NotNull Class<T> entityClass);

    <T> void update(Class<T> entityClass, T object);

    interface Opportunity<T> {

        T attempt(DataChangeListener listener);

    }

    interface DataChangeListener<T> {

        void notifyUpdate(T object, int version);

    }

}
