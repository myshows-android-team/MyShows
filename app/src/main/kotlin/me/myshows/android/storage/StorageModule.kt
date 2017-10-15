package me.myshows.android.storage

import android.content.Context
import dagger.Module
import dagger.Provides
import me.myshows.android.storage.impl.PreferencesTokenStorage
import javax.inject.Singleton

@Module
open class StorageModule {

    @Singleton
    @Provides
    open fun tokenStorage(context: Context): TokenStorage = PreferencesTokenStorage(context)
}
