package com.example.mobilefinalproject

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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

  private fun Restart() {
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
      LocationServices.getFusedLocationProviderClient(applicationContext)
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

          Main(locationModel, darkThemeState, languageState, languageManager, ::Restart)
        }
      }
    }
  }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Main(
  locationModel: LocationViewModel,
  darkThemeState: MutableState<Boolean>,
  languageState: MutableState<String?>,
  languageManager: LanguageManager,
  restartApp: () -> Unit
) {
  val navController = rememberNavController()
  val retrofitInstance = RetrofitInstance.apiService

  var currentWeather by remember { mutableStateOf<WeatherResponse?>(null) }
  var forecastWeather by remember { mutableStateOf<ForecastResponse?>(null) }
  var isLoading by rememberSaveable { mutableStateOf(true) }

  val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

  val lat = locationModel.currentLocation?.latitude
  val lon = locationModel.currentLocation?.longitude
  Log.d("Location", "Lat: $lat, Long: $lon")

  if (lat != null && lon != null) {
    LaunchedEffect(lat, lon) {
      try {
        currentWeather = retrofitInstance.getWeather(lon, lat, "metric", BuildConfig.API_KEY)
        forecastWeather = retrofitInstance.getForecast(lon, lat, 40, "metric", BuildConfig.API_KEY)
        isLoading = false
      } catch (e: LocationClient.LocationException) {
        isLoading = false
        println("Location Exception: ${e.message}")
      } catch (e: Exception) {
        isLoading = false
        println("General Exception: ${e.message}")
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    NavHost(
      navController = navController,
      startDestination = "Today",
    ) {
      composable("Today") { HomeScreen(navController, currentWeather) }
      composable("5 Days") { ForecastScreen(navController, forecastWeather) }
      composable("Settings") {
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

    if (isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
      ){
        CircularProgressIndicator(
          modifier = Modifier.align(Alignment.Center),
          strokeCap = StrokeCap.Round
        )
      }
    }
  }
}