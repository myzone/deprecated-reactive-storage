package com.myzone.reactivestorage.remote.handlers.gson;

import com.google.gson.*;
import com.myzone.annotations.NotNull;

import java.lang.reflect.Type;

import static com.myzone.reactivestorage.remote.handlers.ImmutableRemoteDataObjects.ImmutableRemotePrimitive;
import static com.myzone.reactivestorage.remote.handlers.RemoteDataObject.RemotePrimitive;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemotePrimitiveJsonAdapter extends RemoteDataObjectJsonAdapter implements JsonSerializer<RemotePrimitive>, JsonDeserializer<RemotePrimitive> {

    public static final @NotNull String TYPE_VALUE = "raw";

    public static final @NotNull String DATA_PROPERTY_NAME = "data";

    public @Override JsonElement serialize(RemotePrimitive src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = super.serialize(src, typeOfSrc, context);

        jsonObject.add(TYPE_PROPERTY_NAME, context.serialize(TYPE_VALUE));
        jsonObject.add(DATA_PROPERTY_NAME, context.serialize(src.getData()));

        return jsonObject;
    }

    public @Override RemotePrimitive deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        return new ImmutableRemotePrimitive(context.<String>deserialize(jsonObject.get(COLLECTION_NAME_PROPERTY_NAME), String.class), context
                .<String>deserialize(jsonObject.get(DATA_PROPERTY_NAME), String.class));
    }

}