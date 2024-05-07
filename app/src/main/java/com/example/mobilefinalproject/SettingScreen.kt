package com.example.mobilefinalproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mobilefinalproject.navigation.BottomNavbar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingScreen(navController: NavController, permissionState: PermissionState) {
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
        .padding(padVal),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    )
    {
      ElevatedButton(
        onClick = {
          permissionState.launchPermissionRequest()
        },
        enabled = !permissionState.status.isGranted
      ) {
        Text(
          text = "Request Permissions"
        )
      }
    }
  }
}