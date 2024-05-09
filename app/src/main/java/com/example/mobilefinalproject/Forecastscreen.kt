package com.example.mobilefinalproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WindPower
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilefinalproject.dataclass.ForcastMain
import com.example.mobilefinalproject.dataclass.ForecastResponse
import com.example.mobilefinalproject.dataclass.ForecastWind
import com.example.mobilefinalproject.dataclass.WeatherItem
import com.example.mobilefinalproject.navigation.BottomNavbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

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
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ){
      Column {
        Text(
          text = dateLabel,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Row {
          Icon(
            imageVector = Icons.Outlined.DateRange,
            contentDescription = "date",
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            modifier = Modifier.width(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = forecastDate.format(DateTimeFormatter.ofPattern("dd.MM")),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
          )
        }
      }

      Column {
        Row {
          Icon(
            imageVector = Icons.Outlined.ArrowUpward,
            contentDescription = "max",
            modifier = Modifier.width(15.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "${weatherItem.main.temp_max.roundToInt()} °C",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(modifier = Modifier.width(50.dp))
        Spacer(modifier = Modifier.height(5.dp))
        
        Row {
          Icon(
            imageVector = Icons.Outlined.ArrowDownward,
            contentDescription = "max",
            modifier = Modifier.width(15.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "${weatherItem.main.temp_min.roundToInt()} °C",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(40.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ){
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
          Text(
            "${weatherItem.main.temp.roundToInt()}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
          )
          Text(
            "°C",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
          )
        }
        Text(
          text = weatherItem.weather[0].main,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
        )
      }

      Row {
          Column(
            modifier = Modifier
              .clip(RoundedCornerShape(15.dp))
              .background(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
              .border(2.dp, Color.Transparent, RoundedCornerShape(15.dp))
              .padding(10.dp)
          ) {
            Text(
              text = stringResource(id = R.string.humid),
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row {
              Icon(
                imageVector = Icons.Outlined.WaterDrop,
                contentDescription = "humidity",
                modifier = Modifier.width(20.dp),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                "${weatherItem.main.humidity}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = " %",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))
        
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clip(RoundedCornerShape(15.dp))
              .background(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
              .border(2.dp, Color.Transparent, RoundedCornerShape(15.dp))
              .padding(10.dp)
          ) {
            Text(
              text = stringResource(id = R.string.wind),
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row {
              Icon(
                imageVector = Icons.Outlined.WindPower,
                contentDescription = "humidity",
                modifier = Modifier.width(20.dp),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                "${weatherItem.wind.speed.roundToInt()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = " m",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
              )
            }
          }
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

  val groupedByDay = forecastResponse?.list
    ?.groupBy { weatherItem -> LocalDate.parse(weatherItem.dt_txt.substring(0, 10), DateTimeFormatter.ISO_DATE) }
    ?: mapOf()

  val averagedList = groupedByDay.map { (_, weatherItems: List<WeatherItem>) ->
    val avgTemp = weatherItems.map { it.main.temp }.average()
    val avgHumidity = weatherItems.map { it.main.humidity }.average()
    val avgWindSpeed = weatherItems.map { it.wind.speed }.average()
    val avgMaxTemp = weatherItems.map { it.main.temp_max }.average()
    val avgMinTemp = weatherItems.map { it.main.temp_min }.average()

    WeatherItem(
      dt = weatherItems.first().dt,
      main = ForcastMain(
        temp = avgTemp,
        feels_like = weatherItems.first().main.feels_like,
        temp_min = avgMinTemp,
        temp_max = avgMaxTemp,
        pressure = weatherItems.first().main.pressure,
        sea_level = weatherItems.first().main.sea_level,
        grnd_level = weatherItems.first().main.grnd_level,
        humidity = avgHumidity.toInt(),
        temp_kf = weatherItems.first().main.temp_kf
      ),
      weather = weatherItems.first().weather,
      clouds = weatherItems.first().clouds,
      wind = ForecastWind(
        speed = avgWindSpeed,
        deg = weatherItems.first().wind.deg,
        gust = weatherItems.first().wind.gust
      ),
      visibility = weatherItems.first().visibility,
      pop = weatherItems.first().pop,
      rain = weatherItems.first().rain,
      sys = weatherItems.first().sys,
      dt_txt = weatherItems.first().dt_txt
    )
  }

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
      items(averagedList) { weatherItem ->
        WeatherItem(weatherItem = weatherItem)
      }
    }
  }
}

