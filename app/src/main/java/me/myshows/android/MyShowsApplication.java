package me.myshows.android;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.net.CookieManager;

import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.api.impl.PreferenceStorage;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by warrior on 05.08.15.
 */
public class MyShowsApplication extends Application {

    private RefWatcher refWatcher;
    private MyShowsClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        CookieManager cookieManager = new CookieManager();
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(loggingInterceptor)
                .build();

        client = new MyShowsClientImpl.Builder(this)
                .client(okHttpClient)
                .storage(new PreferenceStorage(this, cookieManager.getCookieStore()))
                .observerScheduler(AndroidSchedulers.mainThread())
                .build();

        refWatcher = LeakCanary.install(this);
    }

    public static MyShowsClient getMyShowsClient(@NonNull Context context) {
        MyShowsApplication application = (MyShowsApplication) context.getApplicationContext();
        return application.client;
    }

    public static RefWatcher getRefWatcher(@NonNull Context context) {
        MyShowsApplication application = (MyShowsApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
