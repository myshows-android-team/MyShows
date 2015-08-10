package me.myshows.android.model.serialization;

import java.io.IOException;
import java.util.List;

public interface Marshaller {

    <T> byte[] serialize(T object) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

    <E extends List, T> List<T> deserializeList(byte[] bytes, Class<E> listClass, Class<T> elementClass) throws IOException;
}
