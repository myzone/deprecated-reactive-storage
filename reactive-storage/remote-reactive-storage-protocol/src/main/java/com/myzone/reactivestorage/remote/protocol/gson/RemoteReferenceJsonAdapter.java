package com.myzone.reactivestorage.remote.protocol.gson;

import com.myzone.annotations.NotNull;

import java.lang.reflect.Type;

import static com.myzone.reactivestorage.remote.protocol.ImmutableRemoteDataObjects.ImmutableRemoteReference;
import static com.myzone.reactivestorage.remote.protocol.RemoteDataObject.RemoteReference;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemoteReferenceJsonAdapter extends RemoteDataObjectJsonAdapter implements JsonSerializer<RemoteReference>, JsonDeserializer<RemoteReference> {

    public static final @NotNull String TYPE_VALUE = "ref";

    public static final @NotNull String ID_PROPERTY_NAME = "id";

    public @Override @NotNull JsonElement serialize(@NotNull RemoteReference src, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        JsonObject jsonObject = super.serialize(src, typeOfSrc, context);

        jsonObject.add(TYPE_PROPERTY_NAME, context.serialize(TYPE_VALUE));
        jsonObject.add(ID_PROPERTY_NAME, context.serialize(src.getId()));

        return jsonObject;
    }

    public @Override @NotNull RemoteReference deserialize(@NotNull JsonElement json, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        return new ImmutableRemoteReference(
                context.<String>deserialize(jsonObject.get(COLLECTION_NAME_PROPERTY_NAME), String.class),
                context.<String>deserialize(jsonObject.get(ID_PROPERTY_NAME), String.class)
        );
    }

}
