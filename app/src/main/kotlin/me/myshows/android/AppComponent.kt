package me.myshows.android

import dagger.Component
import me.myshows.android.api.ApiModule
import me.myshows.android.api2.Api2Module
import me.myshows.android.net.NetModule
import me.myshows.android.storage.StorageModule
import me.myshows.android.ui.activities.*
import me.myshows.android.ui.fragments.FriendsFragment
import me.myshows.android.ui.fragments.MyShowsFragment
import me.myshows.android.ui.fragments.RatingsFragment
import me.myshows.android.ui.fragments.SettingsFragment
import me.myshows.android.ui.login.LoginPresenter
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        NetModule::class,
        StorageModule::class,
        ApiModule::class,
        Api2Module::class
))
interface AppComponent {

    // TODO: try use dagger.android
    // see https://google.github.io/dagger/android.html and https://habrahabr.ru/post/335940/

    fun inject(activity: MainActivity)
    fun inject(activity: ShowActivity)
    fun inject(activity: CommentsActivity)
    fun inject(activity: EpisodeActivity)
    fun inject(activity: ProfileActivity)

    fun inject(fragment: FriendsFragment)
    fun inject(fragment: MyShowsFragment)
    fun inject(fragment: RatingsFragment)
    fun inject(fragment: SettingsFragment)

    // Only for glide
    fun okHttpClient(): OkHttpClient

    fun newLoginPresenter(): LoginPresenter
}
