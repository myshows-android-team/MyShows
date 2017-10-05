package me.myshows.android.api.jsonrpc

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.myshows.android.api.BaseMockWebServerTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.HttpURLConnection

class JsonRPCTest : BaseMockWebServerTest() {

    private val mapper: ObjectMapper = jacksonObjectMapper()
    private lateinit var api: TestApi

    @Before
    fun setup() {
        api = Retrofit.Builder()
                .addConverterFactory(JsonRPCConverterFactory.create())
                .baseUrl(resolve("/"))
                .build()
                .create(TestApi::class.java)
    }

    @Test
    fun correctJsonRpcMethod() {
        val value = "foo"
        val response = api.method1(Request(value)).execute()
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_OK)
        assertThat(response.body())
                .isNotNull()
                .isInstanceOf(Ok::class.java)
        assertThat((response.body() as Ok).value.result).isEqualTo(value)
    }

    @Test
    fun unknownJsonRpcMethod() {
        val value = "foo"
        val response = api.method2(Request(value)).execute()
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_OK)
        assertThat(response.body())
                .isNotNull()
                .isInstanceOf(Err::class.java)
    }

    @Test(expected = Exception::class)
    fun incorrectJsonRpcMethod() {
        api.method3(Request("foo")).execute()
    }

    @Test(expected = Exception::class)
    fun wrongReturnType() {
        api.method3(Request("foo")).execute()
    }

    override fun response(request: RecordedRequest): MockResponse = when (request.path) {
        "/rpc" -> {
            val requestObject = request.body.into<JsonRPCRequestObject<Request>>()
            val responseObject = if (requestObject.method == METHOD_NAME) {
                JsonRPCResponseObject(Response(requestObject.param.value))
            } else {
                JsonRPCResponseObject(null, Error(400, "Method not found", null))
            }

            MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .setBody(mapper.writeValueAsString(responseObject))
        }
        else -> notFound()
    }

    private inline fun <reified T: Any> Buffer.into(): T = mapper.readValue(inputStream())

    companion object {
        private const val METHOD_NAME: String = "test.method"
        private const val UNKNOWN_METHOD_NAME: String = "test.unwnown-method"
    }

    data class Request(@JsonProperty("value") val value: String)
    data class Response(@JsonProperty("result") val result: String)

    interface TestApi {

        @JsonRPCMethod(METHOD_NAME)
        @POST("/rpc")
        fun method1(@Body request: Request): Call<JsonRPCResult<Response>>

        @JsonRPCMethod(UNKNOWN_METHOD_NAME)
        @POST("/rpc")
        fun method2(@Body request: Request): Call<JsonRPCResult<Response>>

        @POST("/rpc")
        fun method3(@Body request: Request): Call<JsonRPCResult<Response>>

        @JsonRPCMethod(METHOD_NAME)
        @POST("/rpc")
        fun method4(@Body request: Request): Call<Response>
    }
}
