package me.myshows.android.model.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonMarshaller implements Marshaller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return objectMapper.readValue(bytes, clazz);
    }
}
