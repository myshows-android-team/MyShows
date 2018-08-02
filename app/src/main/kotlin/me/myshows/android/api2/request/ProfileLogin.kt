package me.myshows.android.api2.request

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileLogin(@JsonProperty("login") val login: String?) {
    companion object {
        private val EMPTY: ProfileLogin = ProfileLogin(null)

        fun new(login: String?): ProfileLogin = if (login != null) ProfileLogin(login) else EMPTY
    }
}
