package com.example.mvvm

import android.util.Log
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object NetworkManager {
    val api by lazy { retrofit.create<MovieApi>() }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(client)
            .baseUrl("https://www.kobis.or.kr/kobisopenapi/")
            .build()
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            chain.request()
                .newBuilder()
                .build()
                .let { request ->
                    chain.proceed(request)
                }
        }.build()
    }
}
