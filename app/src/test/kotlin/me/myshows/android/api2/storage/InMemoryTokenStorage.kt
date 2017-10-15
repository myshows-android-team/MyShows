package me.myshows.android.api2.storage

import me.myshows.android.storage.TokenStorage
import me.myshows.android.storage.Tokens

class InMemoryTokenStorage : TokenStorage {

    private var tokens: Tokens? = null

    override fun put(tokens: Tokens) {
        this.tokens = tokens
    }

    override fun get(): Tokens? = tokens

    override fun clear() {
        tokens = null
    }
}
