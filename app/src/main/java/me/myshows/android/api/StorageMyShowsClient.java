package me.myshows.android.api;

import java.util.HashSet;

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
        storage.putCookies(new HashSet<>()); // reset cookie otherwise API returns 401
        return authentication(storage.getCredentials());
    }

    public void cleanStorage() {
        storage.clean();
    }
}
