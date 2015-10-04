package me.myshows.android;

import android.app.Application;

import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.api.impl.PreferenceStorage;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by warrior on 05.08.15.
 */
public class MyShowsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyShowsClientImpl.init(this, new PreferenceStorage(this), AndroidSchedulers.mainThread());
    }
}
