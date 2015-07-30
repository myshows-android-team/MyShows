package me.myshows.android.model.serialization;

import java.io.IOException;

public interface Marshaller {

    <T> byte[] serialize(T object) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
