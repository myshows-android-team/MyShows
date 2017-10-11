package me.myshows.android.model.serialization;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.List;

public class JsonMarshaller implements Marshaller {

    private final ObjectMapper objectMapper;

    public JsonMarshaller(@NonNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
