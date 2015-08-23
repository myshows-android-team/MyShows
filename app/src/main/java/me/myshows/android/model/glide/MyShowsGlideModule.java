package me.myshows.android.model.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by warrior on 02.08.15.
 */
public class MyShowsGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(ShowImage.class, InputStream.class, new ShowImageLoader.LoaderFactory());
    }
}
