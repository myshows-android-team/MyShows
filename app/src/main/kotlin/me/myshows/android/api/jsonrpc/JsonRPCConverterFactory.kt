package me.myshows.android.api.jsonrpc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class JsonRPCConverterFactory private constructor(private val mapper: ObjectMapper) : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                       retrofit: Retrofit): Converter<ResponseBody, *>? {
        if (annotations.none { it is JsonRPCMethod }) return null

        val returnType = mapper.typeFactory.constructType(type)
        if (!returnType.hasRawClass(Result::class.java)) return null
        val resultType = returnType.bindings.getBoundType(0) ?: return null
        val errorType = returnType.bindings.getBoundType(1) ?: return null
        if (!errorType.hasRawClass(Error::class.java)) return null

        val javaType = mapper.typeFactory.constructParametricType(JsonRPCResponseObject::class.java, resultType)
        val reader = mapper.readerFor(javaType)
        return JsonRPCResponseBodyConverter<Any>(reader)
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<out Annotation>,
                                      methodAnnotations: Array<out Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
        val methodAnnotation = methodAnnotations.find { it is JsonRPCMethod } as? JsonRPCMethod ?: return null
        return JsonRPCRequestBodyConverter<Any>(mapper, methodAnnotation.method)
    }

    companion object {
        @JvmStatic
        fun create(objectMapper: ObjectMapper = jacksonObjectMapper()): JsonRPCConverterFactory =
                JsonRPCConverterFactory(objectMapper)
    }
}

private class JsonRPCRequestBodyConverter<T>(
        private val mapper: ObjectMapper,
        private val method: String
) : Converter<T, RequestBody> {

    override fun convert(value: T): RequestBody {
        val requestObject = JsonRPCRequestObject(method, value)
        val bytes = mapper.writeValueAsBytes(requestObject)
        return RequestBody.create(MEDIA_TYPE, bytes)
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    }
}

private class JsonRPCResponseBodyConverter<T>(private val reader: ObjectReader) : Converter<ResponseBody, JsonRPCResult<T>> {

    override fun convert(value: ResponseBody): JsonRPCResult<T> {
        val responseObject = value.use { reader.readValue<JsonRPCResponseObject<T>>(it.charStream()) }
        return when {
            responseObject.result != null -> Ok(responseObject.result)
            responseObject.error != null -> Err(responseObject.error)
            else -> error("Unreachable")
        }
    }
}
