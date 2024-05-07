package com.example.mobilefinalproject.dataclass

data class ForecastResponse(
  val cod: String,
  val message: Int,
  val cnt: Int,
  val list: List<WeatherItem>,
  val city: City
)

data class WeatherItem(
  val dt: Long,
  val main: ForcastMain,
  val weather: List<Weather>,
  val clouds: Clouds,
  val wind: ForecastWind,
  val visibility: Int,
  val pop: Double,
  val rain: Rain?,
  val sys: ForecastSys,
  val dt_txt: String
)

data class ForcastMain(
  val temp: Double,
  val feels_like: Double,
  val temp_min: Double,
  val temp_max: Double,
  val pressure: Int,
  val sea_level: Int,
  val grnd_level: Int,
  val humidity: Int,
  val temp_kf: Double
)

data class ForecastWind(
  val speed: Double,
  val deg: Int,
  val gust: Double
)

data class Rain(
  val h3: Double
)

data class ForecastSys(
  val pod: String
)

data class City(
  val id: Int,
  val name: String,
  val coord: Coord,
  val country: String,
  val population: Int,
  val timezone: Int,
  val sunrise: Long,
  val sunset: Long
)