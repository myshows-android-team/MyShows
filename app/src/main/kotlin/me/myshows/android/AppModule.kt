package me.myshows.android

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule(private val application: MyShowsApplication) {

    @Singleton
    @Provides
    open fun context(): Context = application
}
