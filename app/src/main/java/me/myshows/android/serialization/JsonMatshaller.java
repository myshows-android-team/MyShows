package me.myshows.android.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonMatshaller implements Marshaller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] serialize(Object object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return objectMapper.readValue(bytes, clazz);
    }
}
