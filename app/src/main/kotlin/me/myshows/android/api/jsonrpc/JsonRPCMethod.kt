package me.myshows.android.api.jsonrpc

@Target(AnnotationTarget.FUNCTION)
annotation class JsonRPCMethod(val method: String)
