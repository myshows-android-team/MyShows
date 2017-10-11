package me.myshows.android

import dagger.Component
import me.myshows.android.api.ApiModule
import me.myshows.android.ui.activities.*
import me.myshows.android.ui.fragments.FriendsFragment
import me.myshows.android.ui.fragments.MyShowsFragment
import me.myshows.android.ui.fragments.RatingsFragment
import me.myshows.android.ui.fragments.SettingsFragment
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class, ApiModule::class))
interface AppComponent {

    // TODO: try use dagger.android
    // see https://google.github.io/dagger/android.html and https://habrahabr.ru/post/335940/

    fun inject(activity: MainActivity)
    fun inject(activity: ShowActivity)
    fun inject(activity: CommentsActivity)
    fun inject(activity: EpisodeActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: ProfileActivity)

    fun inject(fragment: FriendsFragment)
    fun inject(fragment: MyShowsFragment)
    fun inject(fragment: RatingsFragment)
    fun inject(fragment: SettingsFragment)

    // Only for glide
    fun okHttpClient(): OkHttpClient
}
