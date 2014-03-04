package com.myzone.reactivestorage.remote;

import com.myzone.reactivestorage.remote.api.ClientApi;
import com.myzone.reactivestorage.remote.api.ServerApi;

/**
 * @author myzone
 * @date 25.01.14
 */
public class ServerEndpointHandler {

    protected final ServerApi serverApi;
    protected final ClientApi clientApi;

    public ServerEndpointHandler(ServerApi serverApi, ClientApi clientApi) {
        this.serverApi = serverApi;
        this.clientApi = clientApi;
    }


}
