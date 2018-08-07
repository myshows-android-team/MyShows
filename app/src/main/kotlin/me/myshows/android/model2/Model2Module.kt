package me.myshows.android.model2

import android.content.Context
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import me.myshows.android.model.EpisodeRating
import me.myshows.android.model.serialization.EpisodeRatingDeserializer
import javax.inject.Singleton

@Module
open class Model2Module(context: Context) {

    private val configuration: RealmConfiguration

    init {
        Realm.init(context)

        configuration = RealmConfiguration.Builder()
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(configuration)
    }

    @Singleton
    @Provides
    open fun realmConfiguration(): RealmConfiguration = configuration

    @Singleton
    @Provides
    open fun objectMapper(): ObjectMapper {
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
        return jacksonObjectMapper().registerModule(module)
    }
}
