package com.doit.test.core.di

import com.doit.test.core.di.anotations.CoreScope
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class HttpModule {

    private val clientBuilder: OkHttpClient.Builder by lazy {
        val defaultTimeout = 10L
        val defaultUnit = TimeUnit.SECONDS
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .connectTimeout(defaultTimeout, defaultUnit)
            .readTimeout(defaultTimeout, defaultUnit)
            .writeTimeout(defaultTimeout, defaultUnit)
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(
                object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        return chain.proceed(
                            chain.request()
                                .newBuilder()
                                .addHeader("ContentScope-Type", "application/json")
                                .build()
                        )
                    }
                }
            )
    }

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl("http://testapi.doitserver.in.ua/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @CoreScope
    fun provideClientBuilder() = this.clientBuilder

    @Provides
    @CoreScope
    fun provideRetrofitBuilder() = this.retrofitBuilder

}