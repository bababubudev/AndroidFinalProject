package com.example.mobilefinalproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilefinalproject.components.Header
import com.example.mobilefinalproject.dataclass.ForecastResponse
import com.example.mobilefinalproject.dataclass.WeatherItem
import com.example.mobilefinalproject.navigation.BottomNavbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeatherItem(weatherItem: WeatherItem) {

  val forecastDate = LocalDate.parse(weatherItem.dt_txt.substring(0, 10), DateTimeFormatter.ISO_DATE)
  val currentDate = LocalDate.now()
  val daysDifference = forecastDate.toEpochDay() - currentDate.toEpochDay()

  val dateLabel = when (daysDifference) {
    0L -> "Today"
    1L -> "Tomorrow"
    else -> "$daysDifference Days"
  }

  Column(
    modifier = Modifier.padding(16.dp).fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = dateLabel,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text("${weatherItem.main.temp} °C")
    Text("${weatherItem.main.humidity} %")
    Text("${weatherItem.wind.speed} m/s")
  }
  
  Spacer(modifier = Modifier.height(10.dp))
  HorizontalDivider()
  Spacer(modifier = Modifier.height(10.dp))

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(navController: NavController, forecastResponse: ForecastResponse?) {
  var selectedItem by rememberSaveable { mutableIntStateOf(1) }
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

  val displayedDates = mutableSetOf<LocalDate>()

  Scaffold(
    modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
    bottomBar = {
      BottomNavbar(
        navController = navController,
        selectedItem = selectedItem,
        onItemSeleted = { ind -> selectedItem = ind }
      )
    },
    topBar = {
      TopAppBar(
        title = { Header(text = "Forecast for 5 days") },
        navigationIcon = {
          IconButton(onClick = { navController.navigate("Today") }) {
            Icon(
              imageVector = Icons.Outlined.ArrowBackIosNew,
              contentDescription = "Back home"
            )
          }
        },
        scrollBehavior = scrollBehavior,
      )
    }
  ) { padVal ->
    LazyColumn(
      modifier = Modifier.padding(padVal)
    ) {
      items(forecastResponse?.list ?: listOf()) { weatherItem ->
        val forecastDate = LocalDate.parse(weatherItem.dt_txt.substring(0, 10), DateTimeFormatter.ISO_DATE)

        if (!displayedDates.contains(forecastDate)) {
          WeatherItem(weatherItem = weatherItem)
          displayedDates.add(forecastDate)
        }
      }
    }
  }
}
