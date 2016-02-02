package me.myshows.android.api;

import rx.Observable;

public abstract class StorageMyShowsClient implements MyShowsClient {

    protected final ClientStorage storage;

    public StorageMyShowsClient(ClientStorage storage) {
        this.storage = storage;
    }

    /**
     * Tried to authenticate using storage.
     */
    public Observable<Boolean> authentication() {
        return authentication(storage.getCredentials());
    }

    public void clearStorage() {
        storage.clear();
    }
}
