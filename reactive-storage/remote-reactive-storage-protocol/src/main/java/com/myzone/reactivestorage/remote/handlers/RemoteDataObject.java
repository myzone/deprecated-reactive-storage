package com.myzone.reactivestorage.remote.handlers;

import java.util.Map;

/**
 * @author myzone
 * @date 23.01.14
 */
public interface RemoteDataObject {

    String getCollectionName();

    interface RemotePrimitive extends RemoteDataObject {

        String getData();

    }

    interface RemoteValue extends RemoteDataObject {

        Map<String, RemoteDataObject> getFields();

    }

    interface RemoteReference extends RemoteDataObject {

        String getId();

    }

}
