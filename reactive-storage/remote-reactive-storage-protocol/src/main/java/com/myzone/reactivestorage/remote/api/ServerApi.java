package com.myzone.reactivestorage.remote.api;

import com.myzone.annotations.Callback;
import com.myzone.annotations.NotNull;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.myzone.reactivestorage.remote.protocol.RemoteDataObject.RemoteValue;

/**
 * @author myzone
 * @date 17.01.14
 */
public interface ServerApi {

    void onGetAll(@NotNull String collectionName, @NotNull @Callback Consumer<Set<String>> idsConsumer);

    void onGet(@NotNull String collectionName, @NotNull String id, @NotNull @Callback BiConsumer<RemoteValue, Integer> dataAndVersionConsumer);

    void onUpdate(@NotNull String collectionName, @NotNull String id, @NotNull Integer version, @NotNull RemoteValue data, @NotNull @Callback Consumer<Set<String>> idsConsumer);

}
