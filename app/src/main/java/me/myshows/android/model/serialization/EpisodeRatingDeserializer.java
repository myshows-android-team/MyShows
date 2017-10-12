package me.myshows.android.model.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;

import java.io.IOException;

import me.myshows.android.model.EpisodeRating;

/**
 * Created by Whiplash on 2/21/2016.
 */
public class EpisodeRatingDeserializer extends JsonDeserializer<EpisodeRating> implements ResolvableDeserializer {

    private static final EpisodeRating EMPTY_RATING = new EpisodeRating(0, 0, 0, 0, 0, 0, 0);

    private final JsonDeserializer<EpisodeRating> defaultDeserializer;

    public EpisodeRatingDeserializer(JsonDeserializer<EpisodeRating> defaultDeserializer) {
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public EpisodeRating deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
            return EMPTY_RATING;
        } else {
            return defaultDeserializer.deserialize(parser, context);
        }
    }

    @Override
    public void resolve(DeserializationContext context) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(context);
    }
}
