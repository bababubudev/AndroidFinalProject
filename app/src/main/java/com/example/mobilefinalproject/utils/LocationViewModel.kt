package com.example.mobilefinalproject.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mobilefinalproject.dataclass.LocationData

class LocationViewModel: ViewModel() {
  var currentLocation by mutableStateOf<LocationData?>(null)
    private set

  fun updateLocation(location: LocationData) {
    currentLocation = location
  }
}