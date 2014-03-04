package com.myzone.reactivestorage.remote.protocol;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;
import com.myzone.utils.UtilityClass;

import java.util.Map;

import static com.myzone.reactivestorage.remote.protocol.RemoteDataObject.RemotePrimitive;
import static com.myzone.reactivestorage.remote.protocol.RemoteDataObject.RemoteValue;

/**
 * @author myzone
 * @date 23.01.14
 */
public class ImmutableRemoteDataObjects extends UtilityClass {

    public static @Immutable class ImmutableRemotePrimitive implements RemotePrimitive {

        private final @NotNull String collectionName;
        private final @NotNull String data;

        public ImmutableRemotePrimitive(@NotNull String collectionName, @NotNull String data) {
            this.collectionName = collectionName;
            this.data = data;
        }

        public @Override @NotNull String getCollectionName() {
            return collectionName;
        }

        public @Override @NotNull String getData() {
            return data;
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RemotePrimitive)) return false;

            RemotePrimitive that = (RemotePrimitive) o;

            if (!collectionName.equals(that.getCollectionName())) return false;
            if (!data.equals(that.getData())) return false;

            return true;
        }

        public @Override int hashCode() {
            int result = collectionName.hashCode();
            result = 31 * result + data.hashCode();
            return result;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("collectionName", collectionName)
                    .add("data", data)
                    .toString();
        }

    }

    public static @Immutable class ImmutableRemoteValue implements RemoteValue {

        private final @NotNull String collectionName;
        private final @NotNull Map<String, RemoteDataObject> fields;

        public ImmutableRemoteValue(@NotNull String collectionName, @NotNull Map<String, RemoteDataObject> fields) {
            this.collectionName = collectionName;
            this.fields = ImmutableMap.copyOf(fields);
        }

        public @Override @NotNull String getCollectionName() {
            return collectionName;
        }

        public @Override @NotNull Map<String, RemoteDataObject> getFields() {
            return fields;
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RemoteValue)) return false;

            RemoteValue that = (RemoteValue) o;

            if (!collectionName.equals(that.getCollectionName())) return false;
            if (!fields.equals(that.getFields())) return false;

            return true;
        }

        public @Override int hashCode() {
            int result = collectionName.hashCode();
            result = 31 * result + fields.hashCode();
            return result;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("collectionName", collectionName)
                    .add("fields", fields)
                    .toString();
        }

    }

    public static @Immutable class ImmutableRemoteReference implements RemoteDataObject.RemoteReference {

        private final @NotNull String collectionName;
        private final @NotNull String id;

        public ImmutableRemoteReference(@NotNull String collectionName, @NotNull String id) {
            this.collectionName = collectionName;
            this.id = id;
        }

        public @Override @NotNull String getCollectionName() {
            return collectionName;
        }

        public @Override @NotNull String getId() {
            return id;
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RemoteReference)) return false;

            RemoteReference that = (RemoteReference) o;

            if (!collectionName.equals(that.getCollectionName())) return false;
            if (!id.equals(that.getId())) return false;

            return true;
        }


        public @Override int hashCode() {
            int result = collectionName.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("collectionName", collectionName)
                    .add("id", id)
                    .toString();
        }

    }

}
