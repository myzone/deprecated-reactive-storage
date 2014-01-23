package com.myzone.reactivestorage.remote.handlers.gson;

import com.google.gson.JsonElement;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

import static com.myzone.reactivestorage.remote.handlers.ImmutableRemoteDataObjects.ImmutableRemotePrimitive;
import static org.junit.Assert.assertEquals;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemotePrimitiveJsonAdapterTest extends RemoteDataObjectJsonAdapterTest {

    @Test
    public void testSerialize() throws Exception {
        ImmutableRemotePrimitive actual = new ImmutableRemotePrimitive("foo", "some-data");
        InputStream expected = getClass().getClassLoader().getResourceAsStream("RemotePrimitiveJsonAdapterTest/testSerialize_expected.json");

        assertEquals(gson.fromJson(new InputStreamReader(expected), JsonElement.class), gson.toJsonTree(actual));
    }

    @Test
    public void testDeserialize() throws Exception {
        InputStream actual = getClass().getClassLoader().getResourceAsStream("RemotePrimitiveJsonAdapterTest/testDeserialize_actual.json");

        ImmutableRemotePrimitive expected = new ImmutableRemotePrimitive("foo", "some-data");

        assertEquals(gson.toJsonTree(expected), gson.fromJson(new InputStreamReader(actual), JsonElement.class));
    }


}
