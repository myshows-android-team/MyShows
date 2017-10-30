package me.myshows.android.ui.login

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import me.myshows.android.api.impl.Credentials
import me.myshows.android.api2.auth.MyShowsAuthClient
import me.myshows.android.api2.auth.MyShowsAuthClient.RefreshResult as RR
import me.myshows.android.api2.auth.MyShowsAuthClient.RefreshResult.*
import javax.inject.Inject

class LoginPresenter @Inject constructor(
        private val oldClient: me.myshows.android.api.MyShowsClient,
        private val authClient: MyShowsAuthClient
) : MviBasePresenter<LoginView, LoginViewState>() {

    override fun bindIntents() {
        val autoAuth = authClient.refreshTokens()
                .zipWith(oldClientAutoAuth(), BiFunction<RR, RR, RR>(::minOf))
                .map { result ->
                    when (result) {
                        NOTHING_TO_REFRESH -> NeedManualLogin
                        ERROR -> AutoLoginResult(false)
                        SUCCESS -> AutoLoginResult(true)
                    }
                }.toObservable()


        val authFn: (LoginView.Credentials) -> Single<Boolean> = { (login, password) ->
            authClient.auth(login, password)
                    .zipWith(oldClient.authentication(Credentials.make(login, password)).toV2(), BiFunction(Boolean::and))
            }

        val manualAuth = intent(LoginView::loginIntent)
                .flatMapSingle(authFn)
                .map(::LoginResult)

        val auth = Observable.concat(autoAuth, manualAuth)
                .observeOn(AndroidSchedulers.mainThread())
        subscribeViewState(auth, LoginView::render)
    }

    private fun oldClientAutoAuth(): Single<MyShowsAuthClient.RefreshResult> {
        if (!oldClient.hasCredentials()) return Single.just(NOTHING_TO_REFRESH)
        return oldClient.autoAuthentication()
                .toV2()
                .map { result -> if (result) SUCCESS else ERROR }
    }
}

private fun <T> rx.Single<T>.toV2(): Single<T> = RxJavaInterop.toV2Single(this)
