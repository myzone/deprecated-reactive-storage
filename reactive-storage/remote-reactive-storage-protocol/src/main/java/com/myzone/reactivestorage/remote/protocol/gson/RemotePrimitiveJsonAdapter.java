package com.myzone.reactivestorage.remote.protocol.gson;

import com.myzone.annotations.NotNull;

import java.lang.reflect.Type;

import static com.myzone.reactivestorage.remote.protocol.ImmutableRemoteDataObjects.ImmutableRemotePrimitive;
import static com.myzone.reactivestorage.remote.protocol.RemoteDataObject.RemotePrimitive;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemotePrimitiveJsonAdapter extends RemoteDataObjectJsonAdapter implements JsonSerializer<RemotePrimitive>, JsonDeserializer<RemotePrimitive> {

    public static final @NotNull String TYPE_VALUE = "raw";

    public static final @NotNull String DATA_PROPERTY_NAME = "data";

    public @Override @NotNull JsonElement serialize(@NotNull RemotePrimitive src, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        JsonObject jsonObject = super.serialize(src, typeOfSrc, context);

        jsonObject.add(TYPE_PROPERTY_NAME, context.serialize(TYPE_VALUE));
        jsonObject.add(DATA_PROPERTY_NAME, context.serialize(src.getData()));

        return jsonObject;
    }

    public @Override @NotNull RemotePrimitive deserialize(@NotNull JsonElement json, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        return new ImmutableRemotePrimitive(
                context.<String>deserialize(jsonObject.get(COLLECTION_NAME_PROPERTY_NAME), String.class),
                context.<String>deserialize(jsonObject.get(DATA_PROPERTY_NAME), String.class)
        );
    }

}