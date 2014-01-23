package com.myzone.reactivestorage.remote.handlers.gson;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.myzone.reactivestorage.remote.handlers.RemoteDataObject;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

import static com.myzone.reactivestorage.remote.handlers.ImmutableRemoteDataObjects.*;
import static org.junit.Assert.assertEquals;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemoteValueJsonAdapterTest extends RemoteDataObjectJsonAdapterTest {

    @Test
    public void testSerialize() throws Exception {
        InputStream actual = getClass().getClassLoader().getResourceAsStream("RemoteValueJsonAdapterTest/testSerialize_expected.json");
        ImmutableRemoteValue expected = new ImmutableRemoteValue("foo", ImmutableMap.<String, RemoteDataObject>builder()
                .put("bar", new ImmutableRemotePrimitive("foo", "ololo"))
                .put("baz", new ImmutableRemoteReference("foo", "some-id"))
                .build());

        assertEquals(gson.toJsonTree(expected), gson.fromJson(new InputStreamReader(actual), JsonElement.class));
    }

    @Test
    public void testDeserialize() throws Exception {
        ImmutableRemoteValue actual = new ImmutableRemoteValue("foo", ImmutableMap.<String, RemoteDataObject>builder()
                .put("bar", new ImmutableRemotePrimitive("foo", "ololo"))
                .put("baz", new ImmutableRemoteReference("foo", "some-id"))
                .build());
        InputStream expected = getClass().getClassLoader().getResourceAsStream("RemoteValueJsonAdapterTest/testDeserialize_actual.json");

        assertEquals(gson.fromJson(new InputStreamReader(expected), JsonElement.class), gson.toJsonTree(actual));
    }


}
