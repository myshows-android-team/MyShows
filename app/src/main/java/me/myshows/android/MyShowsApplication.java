package me.myshows.android;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.Realm;
import me.myshows.android.api.ApiModule;
import me.myshows.android.api2.Api2Module;
import me.myshows.android.net.NetModule;
import me.myshows.android.storage.StorageModule;

/**
 * Created by warrior on 05.08.15.
 */
public class MyShowsApplication extends Application {

    private AppComponent appComponent;

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        Realm.init(this);
        refWatcher = LeakCanary.install(this);
        appComponent = buildComponent();
    }

    @NonNull
    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .storageModule(new StorageModule())
                .apiModule(new ApiModule())
                .api2Module(new Api2Module())
                .build();
    }

    @NonNull
    public static AppComponent getComponent(@NonNull Context context) {
        MyShowsApplication application = (MyShowsApplication) context.getApplicationContext();
        return application.appComponent;
    }

    @NonNull
    public static RefWatcher getRefWatcher(@NonNull Context context) {
        MyShowsApplication application = (MyShowsApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
