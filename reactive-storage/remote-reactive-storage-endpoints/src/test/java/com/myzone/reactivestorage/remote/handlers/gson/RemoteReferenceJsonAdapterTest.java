package com.myzone.reactivestorage.remote.handlers.gson;

import com.google.gson.JsonElement;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

import static com.myzone.reactivestorage.remote.handlers.ImmutableRemoteDataObjects.ImmutableRemoteReference;
import static org.junit.Assert.assertEquals;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemoteReferenceJsonAdapterTest extends RemoteDataObjectJsonAdapterTest {

    @Test
    public void testSerialize() throws Exception {
        ImmutableRemoteReference actual = new ImmutableRemoteReference("foo", "some-id");
        InputStream expected = getClass().getClassLoader().getResourceAsStream("RemoteReferenceJsonAdapterTest/testSerialize_expected.json");

        assertEquals(gson.fromJson(new InputStreamReader(expected), JsonElement.class), gson.toJsonTree(actual));
    }

    @Test
    public void testDeserialize() throws Exception {
        InputStream actual = getClass().getClassLoader().getResourceAsStream("RemoteReferenceJsonAdapterTest/testDeserialize_actual.json");
        ImmutableRemoteReference expected = new ImmutableRemoteReference("foo", "some-id");

        assertEquals(gson.toJsonTree(expected), gson.fromJson(new InputStreamReader(actual), JsonElement.class));
    }

}
