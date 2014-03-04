package com.myzone.reactivestorage.remote;

import com.myzone.annotations.NotNull;
import com.myzone.utils.math.Counter;

/**
 * @author myzone
 * @date 15.01.14
 */
public interface DataProvider {

    @NotNull <T> Iterable<@NotNull Opportunity<T>> getAll(@NotNull String collection);

    <T> void update(@NotNull String collection, T object);

    interface Opportunity<T> {

        @NotNull T attempt(DataChangeListener listener);

    }

    interface DataChangeListener<T, C extends Counter<?>> {

        void onUpdateUpdate(T object, C version);

    }

}
