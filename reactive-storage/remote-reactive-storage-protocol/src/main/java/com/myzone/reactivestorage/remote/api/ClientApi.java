package com.myzone.reactivestorage.remote.api;

/**
 * @author myzone
 * @date 17.01.14
 */
public interface ClientApi {

    void onUpdateNotify(String collectionName, String id, String version);

}
