package me.myshows.android.ui.login

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface LoginView : MvpView {

    fun loginIntent(): Observable<Credentials>
    fun render(state: LoginViewState)

    data class Credentials(val login: String, val password: String)
}
