package com.example.notes.weather

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.foundations.GPSUtils
import com.example.notes.models.Weather
import com.example.notes.retrofit.WeatherApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_weather.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    lateinit var weatherService: WeatherApi
    lateinit var currentLatLng: LatLng

    lateinit var weatherViewModel: WeatherViewModel
//    val weathersList = arrayListOf<Weather>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", "backPressedMap")

//        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
//        weatherViewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weather ->
//            weathersList.clear()
//            weathersList.add(weather)
//        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            GPSUtils.REQUEST_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentLatLng = GPSUtils.instance.latLng
                    getCurrentWeather()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GPSUtils.instance.findDeviceLocation(requireActivity())
        currentLatLng = GPSUtils.instance.latLng

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        weatherService = retrofit.create(WeatherApi::class.java)

        if (currentLatLng != null) getCurrentWeather()
    }

    private fun getCurrentWeather() {
        val parameters = mutableMapOf(
            "lat" to currentLatLng.latitude.toString(),
            "lon" to currentLatLng.longitude.toString(),
            "exclude" to arrayListOf("minutely", "hourly", "daily").joinToString(","),
            "appId" to getString(R.string.open_weather_map_key)
        )

        val call = weatherService.getWeather(
            currentLatLng.latitude.toString(),
            currentLatLng.longitude.toString(),
            arrayListOf("minutely", "daily").joinToString(","),
            getString(R.string.open_weather_map_key)
        )

        call.enqueue(object : Callback<Weather> {
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                currentWeatherResult.text = t.message
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<Weather>, response: retrofit2.Response<Weather>) {
                if (!response.isSuccessful) {
                    currentWeatherResult.text = "Code: ${response.code()}"
                    return
                }

                val currentWeather = response.body()

                currentWeather?.let {
                    val current = it.current

                    val date = Date(current.dt * 1000)

                    val temp = String.format("%.2f", current.temp - 273.15)
                    val feels_like = String.format("%.2f", current.feels_like - 273.15)


                    var currentText =
                        "Date: ${date.toString().substringBefore("GMT")} -> Temp: $temp (feels like: $feels_like), Humidity: ${current.humidity}%, Description: ${current.weather[0].description}"

                    currentWeatherResult.text = currentText;

                    val hourly = it.hourly

                    var hourlyText = ""

                    for (x in hourly.indices step 3) {

                        val hour = hourly[x]

                        val date = Date(hour.dt * 1000)
                        val temp = String.format("%.2f", hour.temp - 273.15)
                        val feels_like = String.format("%.2f", hour.feels_like - 273.15)

                        hourlyText += "Date: ${date.toString().substringBefore("GMT")} -> Temp: $temp (feels like: $feels_like), Humidity: ${hour.humidity}%, Description: ${hour.weather[0].description}\n";
                    }

                    hourlyWeatherResult.text = hourlyText
                }

            }
        })
    }
}