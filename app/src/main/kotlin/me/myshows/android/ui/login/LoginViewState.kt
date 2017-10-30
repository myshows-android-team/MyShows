package me.myshows.android.ui.login

sealed class LoginViewState

/**
 * Result of auto login attempt that we do if we have access + refresh token pair at start of application
 */
data class AutoLoginResult(val success: Boolean) : LoginViewState()

/**
 * Should be returned if we doesn't have access token for myshows API.
 * In this case we can show login form immediately
 */
object NeedManualLogin : LoginViewState()

/**
 * Result of user login attempt (after user pressed `Login` button)
 */
data class LoginResult(val success: Boolean) : LoginViewState()
