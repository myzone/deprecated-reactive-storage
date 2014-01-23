package com.myzone.reactivestorage.remote.handlers.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myzone.reactivestorage.remote.handlers.RemoteDataObject;
import org.junit.Before;

/**
 * @author myzone
 * @date 23.01.14
 */
public class RemoteDataObjectJsonAdapterTest {

    protected Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder().registerTypeHierarchyAdapter(RemoteDataObject.RemotePrimitive.class, new RemotePrimitiveJsonAdapter())
                .registerTypeHierarchyAdapter(RemoteDataObject.RemoteValue.class, new RemoteValueJsonAdapter())
                .registerTypeHierarchyAdapter(RemoteDataObject.RemoteReference.class, new RemoteReferenceJsonAdapter())
                .registerTypeAdapterFactory(RemoteDataObjectJsonAdapter.getRuntimeTypeAdapterFactory())
                .setPrettyPrinting()
                .create();
    }

}
