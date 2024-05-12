package com.example.mobilefinalproject

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilefinalproject.dataclass.ForecastResponse
import com.example.mobilefinalproject.dataclass.LocationData
import com.example.mobilefinalproject.dataclass.WeatherResponse
import com.example.mobilefinalproject.dataclass.emptyForecastResponse
import com.example.mobilefinalproject.dataclass.emptyWeatherResponse
import com.example.mobilefinalproject.ui.theme.MobileFinalProjectTheme
import com.example.mobilefinalproject.utils.LocationClient
import com.example.mobilefinalproject.utils.LocationViewModel
import com.example.mobilefinalproject.utils.RetrofitInstance
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wrap
import java.util.Locale

class MainActivity : ComponentActivity() {
  private val locationModel by viewModels<LocationViewModel>()
  private lateinit var locationClient: LocationClient
  private lateinit var languageManager: LanguageManager

  override fun attachBaseContext(newBase: Context) {
    languageManager = LanguageManager(newBase)

    val locale = Locale(languageManager.getLanguage() ?: "en")
    val context = newBase.wrap(locale)
    super.attachBaseContext(context)
  }

  private fun restart() {
    this.recreate()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    ActivityCompat.requestPermissions(
      this,
      arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
      ),
      0
    )

    locationClient = DefaultLocationClient(
      applicationContext,
      LocationServices.getFusedLocationProviderClient(applicationContext),
    )

    languageManager = LanguageManager(this)

    setContent {
      val darkThemeState = rememberSaveable {mutableStateOf(true) }
      val languageState = rememberSaveable { mutableStateOf(languageManager.getLanguage()) }

      MobileFinalProjectTheme(
        darkTheme = darkThemeState.value
      ) {
        Surface(
          modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
          Main(
            locationModel,
            locationClient,
            darkThemeState,
            languageState,
            languageManager,
            ::restart
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Main(
  locationModel: LocationViewModel,
  locationClient: LocationClient,
  darkThemeState: MutableState<Boolean>,
  languageState: MutableState<String?>,
  languageManager: LanguageManager,
  restartApp: () -> Unit
) {
  val navController = rememberNavController()

  var currentWeather by remember { mutableStateOf(emptyWeatherResponse) }
  var forecastWeather by remember { mutableStateOf(emptyForecastResponse) }

  val currentCity = remember { mutableStateOf("") }
  val searchByCity = remember { mutableStateOf(false) }

  val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

  when {
    locationPermissionState.status.isGranted && !searchByCity.value -> {
      val coroutineScope = rememberCoroutineScope()
      LaunchedEffect(Unit) {
        coroutineScope.launch {
          locationClient.getLocationUpdates(10000L)
            .catch {
                e -> e.printStackTrace()
              searchByCity.value = true
            }
            .collect{location ->
              val lat = location.latitude
              val lon = location.longitude
              locationModel.updateLocation(LocationData(lat, lon))
            }
        }

      }

      val lat = locationModel.currentLocation?.latitude
      val lon = locationModel.currentLocation?.longitude

      lat?.let {
        lon?.let {
          LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
              try {
                currentWeather = fetchCurrentWeatherByLocation(lat, lon, languageManager)
                forecastWeather = fetchForecastWeatherByLocation(lat, lon, languageManager)
              } catch (e: LocationClient.LocationException) {
                searchByCity.value = true
              } catch (e: Exception) {
                searchByCity.value = true
                println("General Exception: ${e.message}")
              }
            }
          }
        }
      }
    }
  }

  LaunchedEffect(searchByCity.value) {
    when {
      searchByCity.value && (currentCity.value.isNotEmpty() || currentCity.value.isNotBlank()) -> {
        withContext(Dispatchers.IO) {
          try {
            currentWeather = fetchCurrentWeatherByCity(currentCity.value, languageManager)
            forecastWeather = fetchForecastWeatherByCity(currentCity.value, languageManager)
          } catch (e: Exception) {
            currentCity.value = ""
            println("General Exception: ${e.message}")
          }
        }
      }

      searchByCity.value && (currentCity.value.isEmpty() || currentCity.value.isEmpty()) -> {
        withContext(Dispatchers.IO) {
          try {
            currentWeather = fetchCurrentWeatherByCity("Tampere", languageManager)
            forecastWeather = fetchForecastWeatherByCity("Tampere", languageManager)
          } catch (e: Exception) {
            currentCity.value = ""
            println("General Exception: ${e.message}")
          }
        }
      }
    }
  }


  NavHost(
    navController = navController,
    startDestination = when {
      locationPermissionState.status.isGranted -> "home"
      else -> "setting"
    },
  ) {
    if (locationPermissionState.status.isGranted) {
      composable("home") {
        HomeScreen(
          navController,
          currentWeather,
          currentCity,
          searchByCity,
          restartApp
        )
      }
      composable("forecast") { ForecastScreen(navController, forecastWeather) }
      composable("setting") {
        SettingScreen(
          navController,
          locationPermissionState,
          darkThemeState,
          languageState,
          languageManager,
          restartApp
        )
      }
    }
    else {
      val settingComp: @Composable () -> Unit = {
        SettingScreen(
          navController,
          locationPermissionState,
          darkThemeState,
          languageState,
          languageManager,
          restartApp
        )
      }

      composable("home") { settingComp() }
      composable("forecast") { settingComp() }
      composable("setting") { settingComp() }
    }
  }
}


private suspend fun fetchCurrentWeatherByLocation(lat: Double, lon: Double, languageManager: LanguageManager): WeatherResponse {
  val retrofitInstance = RetrofitInstance.apiService
  return retrofitInstance.getWeather(lon, lat, "metric", BuildConfig.API_KEY, languageManager.getLanguage() ?: "en")
}

private suspend fun fetchForecastWeatherByLocation(lat: Double, lon: Double, languageManager: LanguageManager): ForecastResponse {
  val retrofitInstance = RetrofitInstance.apiService
  return retrofitInstance.getForecast(lon, lat, 40, "metric", BuildConfig.API_KEY, languageManager.getLanguage() ?: "en")
}

private suspend fun fetchCurrentWeatherByCity(city: String, languageManager: LanguageManager): WeatherResponse {
  val retrofitInstance = RetrofitInstance.apiService
  return retrofitInstance.getWeatherByCity(city, "metric", BuildConfig.API_KEY, languageManager.getLanguage() ?: "en")
}

private suspend fun fetchForecastWeatherByCity(city: String, languageManager: LanguageManager): ForecastResponse {
  val retrofitInstance = RetrofitInstance.apiService
  return retrofitInstance.getForecastByCity(city, 40,"metric", BuildConfig.API_KEY, languageManager.getLanguage() ?: "en")
}