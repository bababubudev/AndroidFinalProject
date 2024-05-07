package com.example.mobilefinalproject.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
  private var BASE_URL = "https://api.openweathermap.org/data/2.5/"

  private val retrofit by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  val apiService: APIService by lazy {
    retrofit.create(APIService::class.java)
  }
}