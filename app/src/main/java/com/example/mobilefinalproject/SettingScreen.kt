package com.example.mobilefinalproject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilefinalproject.navigation.BottomNavbar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingScreen(
  navController: NavController,
  permissionState: PermissionState,
  darkThemeState: MutableState<Boolean>,
  languageState: MutableState<String?>,
  languageManager: LanguageManager,
  restartApp: () -> Unit
) {
  var selectedItem by rememberSaveable { mutableIntStateOf(2) }

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
        .padding(horizontal = 40.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    )
    {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(id = R.string.dark_mode),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
        )

        Spacer(modifier = Modifier.width(40.dp))

        Switch(
          checked = darkThemeState.value,
          onCheckedChange = { checked ->
            darkThemeState.value = checked
          },
          thumbContent = {
            Icon(
              imageVector = when {darkThemeState.value ->
                Icons.Default.DarkMode
                else -> Icons.Default.Lightbulb
              },
              contentDescription = "Add icon",
            )
          },
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      HorizontalDivider(modifier = Modifier.width(100.dp))

      Spacer(modifier = Modifier.height(20.dp))

      LanguageSwitch(languageState, languageManager, restartApp)

      Spacer(modifier = Modifier.height(20.dp))

      HorizontalDivider(modifier = Modifier.width(100.dp))

      Spacer(modifier = Modifier.height(20.dp))

      Row {
        ElevatedButton(
          onClick = {
            permissionState.launchPermissionRequest()
          },
          enabled = !permissionState.status.isGranted
        ) {
          Text(
            text = if (permissionState.status.isGranted)
              stringResource(id = R.string.permission_grant) else
              stringResource(id = R.string.permission_req),
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = { restartApp() },
          modifier = Modifier.background(
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
            shape = CircleShape
          )
        ) {
          Icon(
            imageVector = Icons.Default.RestartAlt,
            contentDescription = "restart",
            modifier = Modifier.size(22.dp),
          )
        }
      }
    }
  }
}

@Composable
fun LanguageSwitch(
  languageState: MutableState<String?>,
  languageManager: LanguageManager,
  restartApp: () -> Unit
) {
  val languages = listOf("en", "fi")

  Column(
    modifier = Modifier
      .width(200.dp)
      .padding(vertical = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = stringResource(id = R.string.language),
      color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
    )
    Spacer(modifier = Modifier.height(8.dp))
    languages.forEach { lang ->
      Row(
        Modifier
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        RadioButton(
          selected = languageState.value == lang,
          onClick = {
            restartApp()
            languageState.value = lang
            languageManager.setLanguage(lang)
          }
        )
        Text(
          text = lang.uppercase(Locale.ROOT),
          modifier = Modifier
            .clickable {
              restartApp()
              languageState.value = lang
              languageManager.setLanguage(lang)
            }
            .padding(horizontal = 40.dp, vertical = 15.dp),
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
        )
      }
    }
  }
}