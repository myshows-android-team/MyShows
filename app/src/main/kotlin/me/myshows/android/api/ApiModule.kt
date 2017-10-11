package me.myshows.android.api

import android.content.Context
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import me.myshows.android.api.impl.MyShowsClientImpl
import me.myshows.android.api.impl.PreferenceStorage
import me.myshows.android.model.EpisodeRating
import me.myshows.android.model.serialization.EpisodeRatingDeserializer
import okhttp3.OkHttpClient
import rx.android.schedulers.AndroidSchedulers
import java.net.CookieManager
import javax.inject.Singleton

@Module
open class ApiModule {

    @Singleton
    @Provides
    open fun client(context: Context, okHttpClient: OkHttpClient, cookieManager: CookieManager): MyShowsClient {
        val modifier = object : BeanDeserializerModifier() {
            override fun modifyDeserializer(config: DeserializationConfig, beanDesc: BeanDescription,
                                            deserializer: JsonDeserializer<*>): JsonDeserializer<*> {
                return if (beanDesc.beanClass == EpisodeRating::class.java) {
                    EpisodeRatingDeserializer(deserializer as JsonDeserializer<EpisodeRating>)
                } else {
                    super.modifyDeserializer(config, beanDesc, deserializer)
                }
            }
        }
        val module = SimpleModule().setDeserializerModifier(modifier)
        val mapper = jacksonObjectMapper().registerModule(module)
        val storage = PreferenceStorage(context, cookieManager.cookieStore)
        return MyShowsClientImpl(okHttpClient, mapper, storage, AndroidSchedulers.mainThread())
    }
}
