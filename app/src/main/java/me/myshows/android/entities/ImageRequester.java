package me.myshows.android.entities;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author i.samborskiy
 * @date 01.07.2015
 */
class ImageRequester {

    private static final String TAG = ImageRequester.class.getSimpleName();

    private static final String REQUEST_URL = "http://myshows.me/view/%d/";
    private static final String SELECT_QUERY = "div.presentBlockImg";
    private static final String ATTR_NAME = "style";
    private static final String URL_PREFIX = "http:";
    private static final String URL_SUFFIX = ")";

    private ImageRequester() {
    }

    public static Observable<String> requestImageUrl(int showId) {
        return Observable.just(showId)
                .subscribeOn(Schedulers.io())
                .flatMap(id -> {
                    try {
                        // TODO: persist url before return
                        return Observable.just(getImageUrl(id));
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                        return Observable.just(null);
                    }
                });
    }

    private static String getImageUrl(int showId) throws IOException {
        Document doc = Jsoup.connect(String.format(REQUEST_URL, showId)).get();
        Elements imageDiv = doc.select(SELECT_QUERY);
        String attrValue = imageDiv.attr(ATTR_NAME);
        return attrValue.substring(attrValue.indexOf(URL_PREFIX), attrValue.indexOf(URL_SUFFIX));
    }
}
