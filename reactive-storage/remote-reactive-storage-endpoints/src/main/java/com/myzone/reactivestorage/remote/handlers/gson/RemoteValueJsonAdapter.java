package com.myzone.reactivestorage.remote.handlers.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.myzone.annotations.NotNull;
import com.myzone.reactivestorage.remote.handlers.RemoteDataObject;

import java.lang.reflect.Type;
import java.util.Map;

import static com.myzone.reactivestorage.remote.handlers.ImmutableRemoteDataObjects.ImmutableRemoteValue;
import static com.myzone.reactivestorage.remote.handlers.RemoteDataObject.RemoteValue;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemoteValueJsonAdapter extends RemoteDataObjectJsonAdapter implements JsonSerializer<RemoteValue>, JsonDeserializer<RemoteValue> {

    public static final @NotNull String TYPE_VALUE = "val";

    public static final @NotNull String FIELDS_PROPERTY_NAME = "fields";

    public @Override JsonElement serialize(RemoteValue src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = super.serialize(src, typeOfSrc, context);

        jsonObject.add(TYPE_PROPERTY_NAME, context.serialize(TYPE_VALUE));
        jsonObject.add(FIELDS_PROPERTY_NAME, context.serialize(src.getFields()));

        return jsonObject;
    }

    public @Override RemoteValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        return new ImmutableRemoteValue(context.<String>deserialize(jsonObject.get(COLLECTION_NAME_PROPERTY_NAME), String.class), context
                .<Map<String, RemoteDataObject>>deserialize(jsonObject.get(FIELDS_PROPERTY_NAME), new TypeToken<Map<String, RemoteDataObject>>() {
                }.getType()));
    }

}
