package me.myshows.android;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.api.impl.PreferenceStorage;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by warrior on 05.08.15.
 */
public class MyShowsApplication extends Application {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        MyShowsClientImpl.init(this, new PreferenceStorage(this), AndroidSchedulers.mainThread());
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyShowsApplication application = (MyShowsApplication) context.getApplicationContext();
        return application.refWatcher;
    }

}
