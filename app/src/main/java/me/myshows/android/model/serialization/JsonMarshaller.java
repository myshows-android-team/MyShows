package me.myshows.android.model.serialization;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.List;

import me.myshows.android.model.EpisodeRating;

public class JsonMarshaller implements Marshaller {

    private final ObjectMapper objectMapper;

    public JsonMarshaller() {
        BeanDeserializerModifier modifier = new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (beanDesc.getBeanClass() == EpisodeRating.class) {
                    return new EpisodeRatingDeserializer((JsonDeserializer<EpisodeRating>) deserializer);
                }
                return super.modifyDeserializer(config, beanDesc, deserializer);
            }
        };
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(modifier);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public byte[] serialize(Object object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return objectMapper.readValue(bytes, clazz);
    }

    @Override
    public <E extends List, T> List<T> deserializeList(byte[] bytes, Class<E> listClass, Class<T> elementClass) throws IOException {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType collectionType = typeFactory.constructCollectionType(listClass, elementClass);
        return objectMapper.readValue(bytes, collectionType);
    }
}
