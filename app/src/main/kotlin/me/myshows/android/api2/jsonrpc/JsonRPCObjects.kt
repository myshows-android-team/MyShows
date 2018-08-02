package me.myshows.android.api2.jsonrpc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

const val JSON_RPC_VERSION: String = "2.0"

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonRPCRequestObject<out T>(
        @JsonProperty("method") val method: String,
        @JsonProperty("params") val params: T,
        @JsonProperty("jsonrpc") val jsonrpc: String = JSON_RPC_VERSION,
        @JsonProperty("id") val id: Int = 1
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonRPCResponseObject<out T>(
        @JsonProperty("result") val result: T?,
        @JsonProperty("error") val error: Error? = null,
        @JsonProperty("jsonrpc") val jsonrpc: String = JSON_RPC_VERSION,
        @JsonProperty("id") val id: Int = 1
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Error(
    @JsonProperty("code") val code: Int,
    @JsonProperty("message") val message: String,
    @JsonProperty("data") val data: Any?
)

typealias JsonRPCResult<T> = Result<T, Error>
