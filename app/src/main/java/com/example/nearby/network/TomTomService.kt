package com.example.nearby.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.tomtom.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TomTomService{
    @GET("search/2/nearbySearch/.json")
    suspend fun getNearbyData(@Query("key") key : String, @Query("lat") lat: Double, @Query("lon") lon: Double) : NearbyData
}

object TomTomAPI{
    val retrofitService : TomTomService by lazy { retrofit.create(TomTomService::class.java) }
}