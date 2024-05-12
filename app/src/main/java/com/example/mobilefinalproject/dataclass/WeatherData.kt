package com.example.mobilefinalproject.dataclass

data class WeatherResponse(
  val coord: Coord,
  val weather: List<Weather>,
  val base: String,
  val main: Main,
  val visibility: Int,
  val wind: Wind,
  val clouds: Clouds,
  val dt: Long,
  val sys: Sys,
  val timezone: Int,
  val id: Long,
  var name: String,
  val cod: Int
)

data class Coord(
  val lon: Double,
  val lat: Double
)

data class Weather(
  val id: Int,
  val main: String,
  val description: String,
  val icon: String
)

data class Main(
  val temp: Double,
  val feels_like: Double,
  val temp_min: Double,
  val temp_max: Double,
  val pressure: Int,
  val humidity: Int
)

data class Wind(
  val speed: Double,
  val deg: Int
)

data class Clouds(
  val all: Int
)

data class Sys(
  val type: Int,
  val id: Long,
  val country: String,
  val sunrise: Long,
  val sunset: Long
)

val emptyWeatherResponse = WeatherResponse(
  coord = Coord(0.0, 0.0),
  weather = listOf(
    Weather(
      id = 800,
      main = "Clear",
      description = "clear sky",
      icon = "01d"
    )
  ),
  base = "...",
  main = Main(
    temp = 20.0,
    feels_like = 19.0,
    temp_min = 18.0,
    temp_max = 22.0,
    pressure = 1012,
    humidity = 40
  ),
  visibility = 0,
  wind = Wind(
    speed = 3.6,
    deg = 80
  ),
  clouds = Clouds(
    0
  ),
  dt = 1622986200,
  sys = Sys(
    type = 2,
    id = 2002106,
    country = "FI",
    sunrise = 1622941371,
    sunset = 1623004874
  ),
  timezone = 10800,
  id = 658_225,
  name = "... ...",
  cod = 0
)
