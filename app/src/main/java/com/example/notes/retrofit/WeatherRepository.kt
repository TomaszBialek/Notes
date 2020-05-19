package com.example.notes.retrofit

import com.example.notes.models.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherRepository {

    lateinit var weather: Weather

    private val weatherApi: WeatherApi by lazy {
        RetrofitService.createService(WeatherApi::class.java)
    }

    fun getWeather(lat: String, lon: String, exclude: String, appId: String): Weather {
        weatherApi.getWeather(latitude = lat, longitude = lon, exclude = exclude, appKey = appId).enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if (response.isSuccessful) {
                    weather = response.body()!!
                }
            }

            override fun onFailure(call: Call<Weather?>?, t: Throwable?) {

            }
        })
        return weather
    }

    companion object {
        val weatherRepository: WeatherRepository by lazy {
            WeatherRepository()
        }
    }

}