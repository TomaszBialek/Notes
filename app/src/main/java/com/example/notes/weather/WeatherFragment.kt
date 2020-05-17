package com.example.notes.weather

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
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
import com.example.notes.retrofit.WeatherService
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_weather.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    lateinit var weatherService: WeatherService
    lateinit var currentLatLng: LatLng

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", "backPressedMap")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            GPSUtils.REQUEST_LOCATION -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentLatLng = GPSUtils.instance.latLng
                    getData()
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

        weatherService = retrofit.create(WeatherService::class.java)

        if (currentLatLng != null) getData()
    }

    private fun getData() {
        val parameters = mutableMapOf(
            "lat" to currentLatLng.latitude.toString(),
            "lon" to currentLatLng.longitude.toString(),
            "exclude" to arrayListOf("minutely", "hourly", "daily").joinToString(","),
            "appId" to getString(R.string.open_weather_map_key)
        )

        val call = weatherService.getCurrentWeather(
            currentLatLng.latitude.toString(),
            currentLatLng.longitude.toString(),
            arrayListOf("minutely", "hourly", "daily").joinToString(","),
            getString(R.string.open_weather_map_key)
        )

        call.enqueue(object : Callback<Weather>{
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                textViewResult.text = t.message
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<Weather>, response: retrofit2.Response<Weather>) {
                if (!response.isSuccessful) {
                    textViewResult.text = "Code: ${response.code()}"
                    return
                }

                val currentWeather = response.body()

                currentWeather?.let {
                    var content = "Clouds: ${it.current.clouds}"

                    textViewResult.append(content.toString())
                }

            }
        })
    }
}