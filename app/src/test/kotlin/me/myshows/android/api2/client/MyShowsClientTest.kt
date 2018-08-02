package me.myshows.android.api2.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.myshows.android.api2.BaseMockWebServerTest
import me.myshows.android.api2.client.impl.MyShowsClientImpl
import me.myshows.android.api2.jsonrpc.JsonRPCException
import me.myshows.android.model2.EpisodeComments
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Java6Assertions
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection

class MyShowsClientTest : BaseMockWebServerTest() {

    private lateinit var client: MyShowsClientImpl

    private var authUser: Boolean = false

    @Before
    fun setup() {
        client = MyShowsClientImpl(resolve("/"), OkHttpClient(), jacksonObjectMapper())
    }

    @Test
    fun authShowEpisodeComments() {
        authUser = true

        val result = client.showEpisodeCommentsBlocking()
        val comment = result.comments.first()

        Java6Assertions.assertThat(comment.id).isEqualTo(COMMENT_ID)
        Java6Assertions.assertThat(comment.showId).isEqualTo(SHOW_ID)
        Java6Assertions.assertThat(comment.episodeId).isEqualTo(EPISODE_ID)
    }

    @Test(expected = JsonRPCException::class)
    fun unauthShowEpisodeComments() {
        authUser = false

        client.showEpisodeCommentsBlocking()
    }

    override fun response(request: RecordedRequest): MockResponse = when (request.path) {
        "/v2/rpc/" -> {
            MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .setBody(if (authUser) RESPONSE else ERROR_RESPONSE)
        }
        else -> error("Incorrect path for this test")
    }

    private fun MyShowsClient.showEpisodeCommentsBlocking(): EpisodeComments =
            showsEpisodeComments(EPISODE_ID).blockingGet()

    companion object {
        private const val REQUEST_ID: Int = 1

        private const val COMMENT_ID: Int = 123
        private const val SHOW_ID: Int = 1
        private const val EPISODE_ID: Int = 999

        private const val RESPONSE: String = """
            {
                "jsonrpc": "2.0",
                "result": {
                    "isTracking": false,
                    "count": 1,
                    "newCount": 1,
                    "hasSpoilers": true,
                    "comments": [
                        {
                            "id": $COMMENT_ID,
                            "showId": $SHOW_ID,
                            "episodeId": $EPISODE_ID,
                            "user": {
                                "login": "username",
                                "avatar": "https://media.myshows.me/avatars/normal/e/67/e677e4338cc9bb0e3bab71acc69fd8af.jpg",
                                "wastedTime": 5518,
                                "gender": "f",
                                "isPro": false
                            },
                            "comment": "Hello, world!",
                            "image": null,
                            "parentId": null,
                            "createdAt": "2018-03-19T12:36:39+0300",
                            "statusId": 1,
                            "isNew": true,
                            "isMyPlus": false,
                            "isMyMinus": false,
                            "isMyComment": false,
                            "rating": 7,
                            "isBad": false,
                            "isEditable": false,
                            "isDeleted": false,
                            "language": "en"
                        }
                    ]
                },
                "id": $REQUEST_ID
            }
        """

        private const val ERROR_RESPONSE: String = """
            {
                "jsonrpc": "2.0",
                "id": $REQUEST_ID,
                "error": {
                    "code": 401,
                    "message": "Internal error",
                    "data": "Unauthorized"
                }
            }
        """
    }
}
