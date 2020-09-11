package com.example.mvvm

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("webservice/rest/boxoffice/searchDailyBoxOfficeList.json")
    fun getMovieInformation(
        @Query("key") key: String,
        @Query("targetDt") date: String
    ): Single<Data>
}