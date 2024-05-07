package com.example.mobilefinalproject

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilefinalproject.dataclass.ForecastResponse
import com.example.mobilefinalproject.dataclass.LocationData
import com.example.mobilefinalproject.dataclass.WeatherResponse
import com.example.mobilefinalproject.ui.theme.MobileFinalProjectTheme
import com.example.mobilefinalproject.utils.LocationClient
import com.example.mobilefinalproject.utils.LocationViewModel
import com.example.mobilefinalproject.utils.RetrofitInstance
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.catch

class MainActivity : ComponentActivity() {
  private val locationModel by viewModels<LocationViewModel>()
  private lateinit var locationClient: LocationClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    ActivityCompat.requestPermissions(
      this,
      arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
      ),
      0
    )

    locationClient = DefaultLocationClient(
      applicationContext,
      LocationServices.getFusedLocationProviderClient(applicationContext)
    )

    setContent {
      MobileFinalProjectTheme {
        Surface(
          modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
          LaunchedEffect(Unit) {
            locationClient
              .getLocationUpdates(1000L)
              .catch { e -> e.printStackTrace() }
              .collect { location ->
                val lat = location.latitude
                val lon = location.longitude
                locationModel.updateLocation(LocationData(lat, lon))
              }
          }

          Main(locationModel)
        }
      }
    }
  }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Main(locationModel: LocationViewModel) {
  val navController = rememberNavController()
  val retrofitInstance = RetrofitInstance.apiService

  var currentWeather by remember { mutableStateOf<WeatherResponse?>(null) }
  var forecastWeather by remember { mutableStateOf<ForecastResponse?>(null) }

  val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

  val lat = locationModel.currentLocation?.latitude
  val lon = locationModel.currentLocation?.longitude
  Log.d("Location", "Lat: $lat, Long: $lon")

  if (lat != null && lon != null) {
    LaunchedEffect(locationModel.currentLocation) {
      try {
        currentWeather = retrofitInstance.getWeather(lon, lat, "metric", BuildConfig.API_KEY)
        forecastWeather = retrofitInstance.getForecast(lon, lat, 40, "metric", BuildConfig.API_KEY)

        Log.d("Current weather", currentWeather?.weather?.get(0).toString() ?: "Nothing here")
        Log.d("Forecast weather", forecastWeather?.list?.get(0)?.main?.temp.toString() ?: "Nothing here")
      } catch (e: Exception) {
        e.printStackTrace()
        println("Shit")
      }
    }
  }

  NavHost(
    navController = navController,
    startDestination = "Today",
  ) {
    composable("Today") { HomeScreen(navController, currentWeather) }
    composable("5 Days") { ForecastScreen(navController, forecastWeather) }
    composable("Settings") { SettingScreen(navController, locationPermissionState) }
  }
}