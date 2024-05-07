package com.example.mobilefinalproject.utils

import com.example.mobilefinalproject.dataclass.ForecastResponse
import com.example.mobilefinalproject.dataclass.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
  @GET("weather")
  suspend fun getWeather(
    @Query("lon") lon: Double,
    @Query("lat") lat: Double,
    @Query("units") units: String,
    @Query("appid") appid: String,
    @Query("lang") lang: String = "en"
  ): WeatherResponse

  @GET("forecast")
  suspend fun getForecast(
    @Query("lon") lon: Double,
    @Query("lat") lat: Double,
    @Query("cnt") count: Int,
    @Query("units") units: String,
    @Query("appid") appid: String
  ): ForecastResponse
}
