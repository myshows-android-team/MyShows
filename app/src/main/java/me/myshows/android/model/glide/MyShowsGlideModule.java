package me.myshows.android.model.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

/**
 * Created by warrior on 02.08.15.
 */
@GlideModule
public class MyShowsGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.append(ShowImage.class, InputStream.class, new ShowImageLoader.LoaderFactory());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
