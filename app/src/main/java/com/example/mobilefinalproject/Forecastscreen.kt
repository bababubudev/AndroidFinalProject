package com.example.mobilefinalproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilefinalproject.dataclass.ForecastResponse
import com.example.mobilefinalproject.dataclass.WeatherItem
import com.example.mobilefinalproject.navigation.BottomNavbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeatherItem(weatherItem: WeatherItem) {

  val forecastDate = LocalDate.parse(weatherItem.dt_txt.substring(0, 10), DateTimeFormatter.ISO_DATE)
  val currentDate = LocalDate.now()
  val daysDifference = forecastDate.toEpochDay() - currentDate.toEpochDay()

  val dateLabel = when (daysDifference) {
    0L -> stringResource(R.string.home)
    1L -> stringResource(R.string.tomorrow)
    else -> forecastDate
      .format(DateTimeFormatter.ofPattern("EEEE")
        .withLocale(Locale.getDefault()))
      .replaceFirstChar { if (it.isLowerCase())
        it.titlecase(Locale.getDefault()) else
          it.toString() }
  }

  Column(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth(),
  ) {
    Text(
      text = dateLabel,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(40.dp))

    Row{
      Column {
        Text(stringResource(R.string.temp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        Text("${weatherItem.main.temp} °C")
      }
      Spacer(modifier = Modifier.weight(1f))
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.humid), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        Text("${weatherItem.main.humidity} %")
      }
      Spacer(modifier = Modifier.weight(1f))
      Column(horizontalAlignment = Alignment.End) {
        Text(stringResource(R.string.wind), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        Text("${weatherItem.wind.speed} m")
      }
    }
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

  val filteredList = forecastResponse?.list
    ?.groupBy { weatherItem -> LocalDate.parse(weatherItem.dt_txt.substring(0, 10), DateTimeFormatter.ISO_DATE) }
    ?.values
    ?.map { it.first() }
    ?: listOf()

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(scrollBehavior.nestedScrollConnection),
    bottomBar = {
      BottomNavbar(
        navController = navController,
        selectedItem = selectedItem,
        onItemSeleted = { ind -> selectedItem = ind }
      )
    },
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.forecast_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
        },
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
      items(filteredList) { weatherItem ->
        WeatherItem(weatherItem = weatherItem)
      }
    }
  }
}

