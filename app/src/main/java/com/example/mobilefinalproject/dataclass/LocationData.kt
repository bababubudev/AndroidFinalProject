package com.example.mobilefinalproject.dataclass

data class LocationData(val latitude: Double, val longitude: Double) {
  override fun equals(other: Any?): Boolean {
    return other is LocationData &&
      other.latitude == latitude &&
      other.longitude == longitude
  }

  override fun hashCode(): Int {
    var result = latitude.hashCode()
    result = 31 * result + longitude.hashCode()
    return result
  }
}