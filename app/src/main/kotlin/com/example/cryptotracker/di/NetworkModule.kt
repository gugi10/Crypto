package com.example.cryptotracker.di

import com.example.cryptotracker.data.model.PricePoint
import com.example.cryptotracker.data.remote.CoinGeckoApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.coingecko.com/api/v3/"

    // Custom Moshi Adapter for CoinGecko's PricePoint arrays
    // CoinGecko returns [[timestamp, price], [timestamp, price], ...]
    // We need to map this to List<PricePoint>
    object PricePointListAdapter {
        @FromJson
        fun fromJson(reader: JsonReader): List<PricePoint> {
            val list = mutableListOf<PricePoint>()
            reader.beginArray()
            while (reader.hasNext()) {
                reader.beginArray()
                val timestamp = reader.nextLong()
                val value = reader.nextDouble()
                list.add(PricePoint(timestamp, value))
                reader.endArray()
            }
            reader.endArray()
            return list
        }

        @ToJson
        fun toJson(writer: JsonWriter, value: List<PricePoint>?) {
            // Serialization not strictly needed for this app if only reading
            writer.beginArray()
            value?.forEach { pricePoint ->
                writer.beginArray()
                writer.value(pricePoint.timestamp)
                writer.value(pricePoint.value)
                writer.endArray()
            }
            writer.endArray()
        }
    }

    // Factory for the PricePointListAdapter to be used by Moshi
    object PricePointListJsonAdapterFactory : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
            if (annotations.isEmpty() && type is ParameterizedType && type.rawType == List::class.java) {
                val elementType = type.actualTypeArguments[0]
                if (elementType == PricePoint::class.java) {
                    return object : JsonAdapter<List<PricePoint>>() {
                        @FromJson
                        override fun fromJson(reader: JsonReader): List<PricePoint>? {
                            // Delegate to the standalone object adapter for actual parsing logic
                            return PricePointListAdapter.fromJson(reader)
                        }

                        @ToJson
                        override fun toJson(writer: JsonWriter, value: List<PricePoint>?) {
                            // Delegate to the standalone object adapter
                            PricePointListAdapter.toJson(writer, value)
                        }
                    }.nullSafe() // It's good practice to make adapters nullSafe
                }
            }
            return null
        }
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Basic client, add logging interceptor for debugging
        val logging = HttpLoggingInterceptor()
        //Timber.tag("OkHttp").d("Creating OkHttpClient") // Example if Timber was set up
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) // Use Level.BASIC or Level.NONE for release
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            // Add other interceptors like for API keys if needed
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(PricePointListJsonAdapterFactory) // Add custom adapter factory for List<PricePoint>
            .add(KotlinJsonAdapterFactory()) // For general Kotlin class support
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinGeckoApiService(retrofit: Retrofit): CoinGeckoApiService {
        return retrofit.create(CoinGeckoApiService::class.java)
    }
}
