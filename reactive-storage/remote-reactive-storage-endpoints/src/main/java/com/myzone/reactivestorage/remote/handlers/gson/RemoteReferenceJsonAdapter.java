package com.myzone.reactivestorage.remote.handlers.gson;

import com.google.gson.*;
import com.myzone.annotations.NotNull;

import java.lang.reflect.Type;

import static com.myzone.reactivestorage.remote.handlers.ImmutableRemoteDataObjects.ImmutableRemoteReference;
import static com.myzone.reactivestorage.remote.handlers.RemoteDataObject.RemoteReference;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemoteReferenceJsonAdapter extends RemoteDataObjectJsonAdapter implements JsonSerializer<RemoteReference>, JsonDeserializer<RemoteReference> {

    public static final @NotNull String TYPE_VALUE = "ref";

    public static final @NotNull String ID_PROPERTY_NAME = "id";

    public @Override JsonElement serialize(RemoteReference src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = super.serialize(src, typeOfSrc, context);

        jsonObject.add(TYPE_PROPERTY_NAME, context.serialize(TYPE_VALUE));
        jsonObject.add(ID_PROPERTY_NAME, context.serialize(src.getId()));

        return jsonObject;
    }

    public @Override RemoteReference deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        return new ImmutableRemoteReference(context.<String>deserialize(jsonObject.get(COLLECTION_NAME_PROPERTY_NAME), String.class), context
                .<String>deserialize(jsonObject.get(ID_PROPERTY_NAME), String.class));
    }

}
