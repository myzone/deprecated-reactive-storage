package com.myzone.reactivestorage.remote.handlers;

/**
 * @author myzone
 * @date 17.01.14.
 */
public interface ClientHandler {

    void onUpdateNotify(String className, String id, String version);

}
