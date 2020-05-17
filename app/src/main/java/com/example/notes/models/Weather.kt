package com.example.notes.models

data class Weather (
    var lat: Double,
    var lon: Double,
    var timezone: String,
    var current: Current
)

data class Current (
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
    var temp: Double,
    var feels_like: Double,
    var pressure: Double,
    var humidity: Int,
    var dew_point: Double,
    var uvi: Double,
    var clouds: Int,
    var visibility: Int,
    var wind_speed: Double,
    var wind_deg: Int,
    var weather: List<WeatherDescription>
)

data class WeatherDescription (
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
)