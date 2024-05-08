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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.google.accompanist.permissions.isGranted
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
      val isLoading = remember { mutableStateOf(false) }
      val loadingString = remember { mutableIntStateOf(R.string.loading) }

      MobileFinalProjectTheme(
        darkTheme = darkThemeState.value
      ) {
        Surface(
          modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
          LaunchedEffect(Unit) {
            isLoading.value = true
            loadingString.intValue = R.string.loading
            locationClient
              .getLocationUpdates(10000L)
              .catch {
                e -> e.printStackTrace()
                loadingString.intValue =  R.string.loading_fail
              }
              .collect { location ->
                val lat = location.latitude
                val lon = location.longitude
                locationModel.updateLocation(LocationData(lat, lon))
                isLoading.value = false
              }
          }

          Main(
            locationModel,
            locationClient,
            darkThemeState,
            languageState,
            languageManager,
            isLoading,
            loadingString,
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
  isLoading: MutableState<Boolean>,
  loadingString: MutableState<Int>,
  restartApp: () -> Unit
) {
  val navController = rememberNavController()
  val retrofitInstance = RetrofitInstance.apiService

  var currentWeather by remember { mutableStateOf<WeatherResponse?>(null) }
  var forecastWeather by remember { mutableStateOf<ForecastResponse?>(null) }

  val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
  if (!locationPermissionState.status.isGranted) {
    isLoading.value = false
  }

  LaunchedEffect(locationPermissionState.status.isGranted) {
    if (locationPermissionState.status.isGranted) {
      isLoading.value = true
      loadingString.value = R.string.loading
      locationClient
        .getLocationUpdates(1000L)
        .catch {
            e -> e.printStackTrace()
          loadingString.value =  R.string.loading_fail
        }
        .collect { location ->
          val lat = location.latitude
          val lon = location.longitude
          locationModel.updateLocation(LocationData(lat, lon))
          isLoading.value = false
        }
    }
  }

  val lat = locationModel.currentLocation?.latitude
  val lon = locationModel.currentLocation?.longitude
  Log.d("Location", "Lat: $lat, Long: $lon")

  if (lat != null && lon != null) {
    LaunchedEffect(Unit) {
      try {
        isLoading.value = true
        loadingString.value = R.string.loading_data
        currentWeather = retrofitInstance.getWeather(lon, lat, "metric", BuildConfig.API_KEY)
        forecastWeather = retrofitInstance.getForecast(lon, lat, 40, "metric", BuildConfig.API_KEY)
        isLoading.value = false
      } catch (e: LocationClient.LocationException) {
        println("Location Exception: ${e.message}")
        loadingString.value = R.string.data_failed
      } catch (e: Exception) {
        isLoading.value = false
        println("General Exception: ${e.message}")
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    NavHost(
      navController = navController,
      startDestination = if (locationPermissionState.status.isGranted) "Today" else "Settings",
    ) {
      if (locationPermissionState.status.isGranted) {
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

        composable("Today") { settingComp() }
        composable("5 Days") { settingComp() }
        composable("Settings") {
          settingComp()
        }
      }


    }

    if (isLoading.value) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.6f))
          .padding(bottom = 40.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          modifier = Modifier.align(Alignment.Center), strokeCap = StrokeCap.Round
        )

        Text(
          text= stringResource(id = loadingString.value),
          modifier = Modifier.padding(top = 80.dp),
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
      }
    }
  }
}
