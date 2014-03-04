package com.myzone.reactivestorage.remote.protocol.gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.myzone.annotations.NotNull;
import com.myzone.reactivestorage.remote.protocol.RemoteDataObject;

import java.lang.reflect.Type;

import static com.myzone.reactivestorage.remote.protocol.RemoteDataObject.*;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemoteDataObjectJsonAdapter {

    public static final @NotNull String COLLECTION_NAME_PROPERTY_NAME = "collection";
    public static final @NotNull String TYPE_PROPERTY_NAME = "type";

    public @NotNull JsonObject serialize(@NotNull RemoteDataObject src, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add(COLLECTION_NAME_PROPERTY_NAME, context.serialize(src.getCollectionName()));

        return jsonObject;
    }

    public static @NotNull RuntimeTypeAdapterFactory<RemoteDataObject> getRuntimeTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory.of(RemoteDataObject.class, TYPE_PROPERTY_NAME)
                .registerSubtype(RemotePrimitive.class, RemotePrimitiveJsonAdapter.TYPE_VALUE)
                .registerSubtype(RemoteValue.class, RemoteValueJsonAdapter.TYPE_VALUE)
                .registerSubtype(RemoteReference.class, RemoteReferenceJsonAdapter.TYPE_VALUE);
    }

}
