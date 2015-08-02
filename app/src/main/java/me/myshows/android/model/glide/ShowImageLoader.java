package me.myshows.android.model.glide;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

/**
 * Created by warrior on 23.08.15.
 */
public class ShowImageLoader extends BaseGlideUrlLoader<ShowImage> {

    private static final String IMAGE_URL = "http://media.myshows.me/shows/%s/%c/%<c%c/%s";
    private static final String NORMAL = "normal";
    private static final String SMALL = "small";

    private static final int NORMAL_WIDTH = 960;
    private static final int NORMAL_HEIGHT = 540;
    private static final int SMALL_WIDTH = 320;
    private static final int SMALL_HEIGHT = 180;

    // TODO: find right multiplier
    private static final int MULTIPLIER = 2;

    public ShowImageLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(ShowImage showImage, int width, int height) {
        if (showImage instanceof QualityShowImage) {
            QualityShowImage qualityShowImage = (QualityShowImage) showImage;
            if (qualityShowImage.getImages() != null && qualityShowImage.getImages().length != 0) {
                String hash = qualityShowImage.getImages()[0];
                String size = width <= MULTIPLIER * SMALL_WIDTH && height <= MULTIPLIER * SMALL_HEIGHT ? SMALL : NORMAL;
                return String.format(IMAGE_URL, size, hash.charAt(0), hash.charAt(1), hash);
            }
        }
        return showImage.getImage();
    }

    public static class LoaderFactory implements ModelLoaderFactory<ShowImage, InputStream> {

        @Override
        public ModelLoader<ShowImage, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new ShowImageLoader(context);
        }

        @Override
        public void teardown() {
        }
    }
}
