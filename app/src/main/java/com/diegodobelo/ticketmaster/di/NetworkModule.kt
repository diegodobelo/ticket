package com.diegodobelo.ticketmaster.di

import com.diegodobelo.ticketmaster.BuildConfig
import com.diegodobelo.ticketmaster.network.TicketMasterService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Named("apiKey")
    fun provideApiKey(): String {
        return BuildConfig.API_KEY
    }

    @Provides
    @Named("baseUrl")
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    @Provides
    fun providesOkHttpInterceptor(
        @Named("apiKey") apiKey: String
    ): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url
            val url = originalUrl
                .newBuilder()
                .addQueryParameter("apikey", apiKey)
                .build()
            val newRequest = originalRequest
                .newBuilder()
                .url(url)
                .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    fun provideOkHttpClient(interceptor: Interceptor) : OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(interceptor)
        return clientBuilder.build()
    }

    @Provides
    fun providesRetrofit(
        @Named("baseUrl") baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideTickerMasterService(retrofit: Retrofit) : TicketMasterService {
        return retrofit.create(TicketMasterService::class.java)
    }
}