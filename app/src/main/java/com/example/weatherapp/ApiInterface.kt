package com.example.weatherapp
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")

    fun getWeatherData(
        @Query("q") city:String,
        @Query("appid") id:String,
        @Query("units") unit:String,
    ) : Call<WeatherApp>

    @GET("weather")
    fun getWeatherDataByLatLon(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") id: String,
    ): Call<WeatherApp>
}