package com.example.mobilefinalproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilefinalproject.dataclass.ToggleableInfo
import com.example.mobilefinalproject.navigation.BottomNavbar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingScreen(navController: NavController, permissionState: PermissionState) {
  var selectedItem by rememberSaveable { mutableIntStateOf(2) }
  var switch by remember {
    mutableStateOf(
      ToggleableInfo(
        isChecked = false,
        text = "Dark mode"
      )
    )
  }

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
        .padding(padVal),
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
          text = "Dark mode",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
        )
        Spacer(modifier = Modifier.width(40.dp))
        Switch(
          checked = switch.isChecked,
          onCheckedChange = { checked ->
            switch = switch.copy(isChecked = checked)
          },
          thumbContent = {
            Icon(
              imageVector = if (switch.isChecked) Icons.Default.Check else Icons.Default.Close,
              contentDescription = "Add icon"
            )
          }
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Language",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
          modifier = Modifier.padding(bottom = 8.dp, end = 100.dp)
        )

        RadioButtons()
      }
      Spacer(modifier = Modifier.height(20.dp))

      ElevatedButton(
        onClick = {
          permissionState.launchPermissionRequest()
        },
        enabled = !permissionState.status.isGranted
      ) {
        Text(
          text = if (permissionState.status.isGranted) "Permission Granted" else "Request Permissions"
        )
      }

    }
  }
}


@Composable
private fun RadioButtons() {
  val radioButtons = remember {
    mutableStateListOf(
      ToggleableInfo(
        isChecked = true,
        text = "English"
      ),
      ToggleableInfo(
        isChecked = false,
        text = "Finnish"
      )
    )
  }

  radioButtons.forEachIndexed { _, info ->
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clickable {
          radioButtons.replaceAll {
            it.copy(isChecked = it.text == info.text)
          }
        }
        .padding(end = 16.dp)
    ) {
      RadioButton(
        selected = info.isChecked,
        onClick = {
          radioButtons.replaceAll {
            it.copy(isChecked = it.text == info.text )
          }
        }
      )
      Text(
        text = info.text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary
      )
    }
  }
}