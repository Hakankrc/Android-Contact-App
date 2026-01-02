package com.hakankirca.myapplication.di

import com.hakankirca.myapplication.data.remote.ApiService
import com.hakankirca.myapplication.data.repository.ContactRepositoryImpl
import com.hakankirca.myapplication.domain.repository.ContactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // RÖNTGEN CİHAZI (Logger)
        val logging = okhttp3.logging.HttpLoggingInterceptor()
        logging.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY) // Tüm cevabı göster

        return OkHttpClient.Builder()
            .addInterceptor(logging) // Logger'ı ekledik
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("ApiKey", com.hakankirca.myapplication.BuildConfig.API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            // DİKKAT: Portu 11235 yaptık ve http yaptık.
            .baseUrl("http://146.59.52.68:11235/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideContactRepository(api: ApiService): ContactRepository {
        return ContactRepositoryImpl(api)
    }
}