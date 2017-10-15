package me.myshows.android.api2.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.myshows.android.api2.BaseMockWebServerTest
import me.myshows.android.api2.MyShowsClient
import me.myshows.android.api2.impl.MyShowsClientImpl
import me.myshows.android.api2.storage.InMemoryTokenStorage
import me.myshows.android.storage.TokenStorage
import me.myshows.android.storage.Tokens
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection

class AuthTest : BaseMockWebServerTest() {

    private val tokenStorage: TokenStorage = InMemoryTokenStorage()
    private lateinit var client: MyShowsClient

    private var shouldFailRequest: Boolean = false

    @Before
    fun setup() {
        client = MyShowsClientImpl(resolve("/"), OkHttpClient(), jacksonObjectMapper(), tokenStorage)
    }

    @After
    fun tearDown() {
        tokenStorage.clear()
    }

    @Test
    fun successfulAuth() {
        shouldFailRequest = false

        val result = client.authBlocking()
        assertThat(result).isEqualTo(true)
        checkResponseTokens()
    }

    @Test
    fun unsuccessfulAuth() {
        shouldFailRequest = true
        tokenStorage.put(INITIAL_TOKENS)

        val result = client.authBlocking()
        assertThat(result).isEqualTo(false)
        checkInitialTokens()
    }

    @Test
    fun refreshWithoutRefreshToken() {
        shouldFailRequest = false

        val result = client.refreshBlocking()
        assertThat(result).isEqualTo(false)
        assertThat(tokenStorage.get()).isNull()
    }

    @Test
    fun successfulRefresh() {
        shouldFailRequest = false
        tokenStorage.put(INITIAL_TOKENS)

        val result = client.refreshBlocking()
        assertThat(result).isEqualTo(true)
        checkResponseTokens()
    }

    @Test
    fun unsuccessfulRefresh() {
        shouldFailRequest = true
        tokenStorage.put(INITIAL_TOKENS)

        val result = client.refreshBlocking()
        assertThat(result).isEqualTo(false)
        checkInitialTokens()
    }

    override fun response(request: RecordedRequest): MockResponse = when (request.path) {
        "/oauth/token" -> {
            if (shouldFailRequest) {
                MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            } else {
                MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .addHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(RESPONSE)
            }
        }
        else -> error("Incorrect path for this test")
    }

    private fun checkResponseTokens() = checkTokens(RESPONSE_ACCESS_TOKEN, RESPONSE_REFRESH_TOKEN)
    private fun checkInitialTokens() = checkTokens(INITIAL_ACCESS_TOKEN, INITIAL_REFRESH_TOKEN)

    private fun checkTokens(expectedAccessToken: String, expectedRefreshToken: String) {
        val (accessToken, refreshToken) = tokenStorage.get() ?: error("Tokens must be not null")
        assertThat(accessToken).isEqualTo(expectedAccessToken)
        assertThat(refreshToken).isEqualTo(expectedRefreshToken)
    }

    private fun MyShowsClient.authBlocking(): Boolean = auth("user", "password")
            .toBlocking().value()
    private fun MyShowsClient.refreshBlocking(): Boolean = refreshTokens().toBlocking().value()

    companion object {
        private const val INITIAL_ACCESS_TOKEN: String = "4847e7c939a210e90b6fffbcf05a2a741ce6c847"
        private const val INITIAL_REFRESH_TOKEN: String = "8a06b8baaa642b623eb553df400fe7f6aa7ffe60"
        private val INITIAL_TOKENS: Tokens = Tokens(INITIAL_ACCESS_TOKEN, INITIAL_REFRESH_TOKEN)

        private const val RESPONSE_ACCESS_TOKEN: String = "9741c0658769a72e6e412b0d9a1571ec5fc3539c"
        private const val RESPONSE_REFRESH_TOKEN: String = "83323d84a80cbf30ecad72f58b53834692b2ef4b"

        private const val RESPONSE: String = """
            {
                "access_token": "$RESPONSE_ACCESS_TOKEN",
                "expires_in": 1209600,
                "token_type": "Bearer",
                "scope": "basic",
                "refresh_token": "$RESPONSE_REFRESH_TOKEN"
            }
        """
    }
}
