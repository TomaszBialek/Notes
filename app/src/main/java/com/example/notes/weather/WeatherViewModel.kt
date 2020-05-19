package com.example.notes.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.models.Weather
import com.example.notes.retrofit.WeatherRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherViewModel(
    val lat: String,
    val lon: String,
    val exclude: String,
    val appId: String
) : ViewModel() {

    private var _weatherLiveData: MutableLiveData<Weather> = MutableLiveData()
    val weatherLiveData: LiveData<Weather> = _weatherLiveData


    val weatherRepository = WeatherRepository.weatherRepository

    init {
        loadData()
    }

    private fun loadData() {
        GlobalScope.launch {
            _weatherLiveData.postValue(weatherRepository.getWeather(lat, lon, exclude, appId))
        }
    }

}