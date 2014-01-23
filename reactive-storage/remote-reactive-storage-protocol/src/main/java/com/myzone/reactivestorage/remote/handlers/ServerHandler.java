package com.myzone.reactivestorage.remote.handlers;

import com.myzone.annotations.Callback;
import com.myzone.annotations.NotNull;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author myzone
 * @date 17.01.14.
 */
public interface ServerHandler {

    void onGetAll(@NotNull String collectionName, @NotNull @Callback Consumer<Set<String>> idsConsumer);

    void onGet(@NotNull String collectionName, @NotNull String id, @NotNull @Callback BiConsumer<Object, Integer> dataAndVersionConsumer);

    void onUpdate(@NotNull String collectionName, @NotNull String id, @NotNull Integer version, @NotNull Object data, @NotNull @Callback Consumer<Set<String>> idsConsumer);


}
