package me.myshows.android.model.glide;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
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

    protected ShowImageLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        super(concreteLoader);
    }

    @Override
    protected String getUrl(ShowImage showImage, int width, int height, Options options) {
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

    @Override
    public boolean handles(ShowImage showImage) {
        return true;
    }

    public static class LoaderFactory implements ModelLoaderFactory<ShowImage, InputStream> {

        // TODO: pass global okhttp client here
        private final ModelLoaderFactory<GlideUrl, InputStream> urlLoaderFactory = new OkHttpUrlLoader.Factory();

        @Override
        public ModelLoader<ShowImage, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new ShowImageLoader(urlLoaderFactory.build(multiFactory));
        }

        @Override
        public void teardown() {
        }
    }
}
