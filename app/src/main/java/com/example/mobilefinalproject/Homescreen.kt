package com.example.mobilefinalproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WindPower
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilefinalproject.components.Header
import com.example.mobilefinalproject.dataclass.WeatherResponse
import com.example.mobilefinalproject.navigation.BottomNavbar
import com.example.mobilefinalproject.navigation.PopupSearchBar

@Composable
fun HomeScreen(navController: NavController, weatherResponse: WeatherResponse?, currentCity: MutableState<String>, restart: () -> Unit) {
  var selectedItem by rememberSaveable { mutableIntStateOf(0) }

  var showSearch by rememberSaveable { mutableStateOf(false) }
  var query by remember { mutableStateOf("") }

  Scaffold(bottomBar = {
    BottomNavbar(
      navController = navController,
      selectedItem = selectedItem,
      onItemSeleted = {ind -> selectedItem = ind }
    )
  }) {padVal ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padVal)
        .padding(horizontal = 20.dp),
    )
    {
      Spacer(modifier = Modifier.height(30.dp))
      if (showSearch) {
        PopupSearchBar(
          query = query,
          onQueryChange = {newQuery -> query = newQuery},
          onExecuteSearch = { currentCity.value = query.trim(); query = ""; showSearch = false },
          onDismiss = {showSearch = false},
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Header(
          text = weatherResponse?.name ?: stringResource(id = R.string.loading)
        )

        IconButton(onClick = {showSearch = !showSearch}) {
          Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "search locations",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
          )
        }
      }

      Row {
        IconButton(
          onClick = { query = ""; restart() },
          modifier = Modifier.size(20.dp)
        ) {
          Icon(
            imageVector = if (currentCity.value.isBlank()) Icons.Outlined.LocationOn else Icons.Outlined.LocationOff,
            contentDescription = "Current location",
            modifier = Modifier.width(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = "${weatherResponse?.coord?.lat?.toInt().toString()}° ${weatherResponse?.coord?.lon?.toInt().toString()}°",
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          textAlign = TextAlign.Center,
        )
      }

      Spacer(modifier = Modifier.height(100.dp))

      Row(
        modifier = Modifier.padding(horizontal = 20.dp)
      ){
        Column {
          Row {
            Text(
              text = weatherResponse?.main?.temp?.toInt().toString(),
              style = MaterialTheme.typography.displayLarge,
              textAlign = TextAlign.Center,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
              text = "°C",
              style = MaterialTheme.typography.titleLarge,
              modifier = Modifier.padding(top = 5.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = weatherResponse?.weather?.get(0)?.main ?: stringResource(id = R.string.loading),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row {
            Row {
              Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = "high")
              Text(text = weatherResponse?.main?.temp_max?.toInt().toString())
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row {
              Icon(imageVector = Icons.Outlined.ArrowDownward, contentDescription = "low")
              Text(text = weatherResponse?.main?.temp_min?.toInt().toString())
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
          horizontalAlignment = Alignment.End
        ) {
          Spacer(modifier = Modifier.height(15.dp))
          
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(15.dp))
              .background(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
              .border(2.dp, Color.Transparent, RoundedCornerShape(15.dp))
              .padding(horizontal = 10.dp, vertical = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.WaterDrop,
              contentDescription = "rain",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = weatherResponse?.main?.humidity.toString() + " %",
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
          }
          
          Spacer(modifier = Modifier.height(10.dp))
          
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(15.dp))
              .background(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
              .border(2.dp, Color.Transparent, RoundedCornerShape(15.dp))
              .padding(horizontal = 10.dp, vertical = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.WindPower,
              contentDescription = "Wind",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = weatherResponse?.wind?.speed.toString() + " m",
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(60.dp))
      HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

      Spacer(modifier = Modifier.height(60.dp))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row {
          Box(
            modifier = Modifier.fillMaxWidth(0.5f),
            contentAlignment = Alignment.Center
          ) {
            Column {
              Text(
                text = stringResource(id = R.string.pressure),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Row {
                Icon(imageVector = Icons.Outlined.Compress, contentDescription = "pressure")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = weatherResponse?.main?.pressure.toString() + " hPa",
                  style = MaterialTheme.typography.titleSmall
                )
              }
            }
          }

          VerticalDivider(
            modifier = Modifier
              .height(50.dp)
              .padding(top = 10.dp)
          )

          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column {
              Text(
                text = stringResource(id = R.string.feel_like),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Row {
                Icon(imageVector = Icons.Default.TagFaces, contentDescription = "feels")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "${weatherResponse?.main?.feels_like?.toInt().toString()} °C    ",
                  style = MaterialTheme.typography.titleSmall
                )
              }
            }

          }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Row {
          Box(
            modifier = Modifier.fillMaxWidth(0.5f),
            contentAlignment = Alignment.Center,
          ) {
            Column{
              Text(
                text = stringResource(id = R.string.cloud_coverage),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
              )
              Spacer(modifier = Modifier.height(10.dp))

              Row {
                Icon(imageVector = Icons.Outlined.Cloud, contentDescription = "cloud")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = weatherResponse?.clouds?.all.toString() + " %",
                  style = MaterialTheme.typography.titleSmall
                )
              }
            }
          }

          VerticalDivider(
            modifier = Modifier
              .height(50.dp)
              .padding(top = 10.dp)
          )

          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column{
              Text(
                text = stringResource(id = R.string.description),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 10.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Row {
                Icon(imageVector = Icons.Outlined.Description, contentDescription = "description")
                Spacer(modifier = Modifier.width(10.dp))
                weatherResponse?.weather?.get(0)?.description?.let {
                  Text(
                    text = it.capitalize(Locale.current), style = MaterialTheme.typography.titleSmall
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}