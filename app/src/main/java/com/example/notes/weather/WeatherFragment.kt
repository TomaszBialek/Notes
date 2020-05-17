package com.example.notes.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.models.Weather
import com.example.notes.retrofit.WeatherService
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", "backPressedMap")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        getData()
    }

    private fun getData() {
        val parameters = mutableMapOf(
            "lat" to "33.441792",
            "lon" to "-94.037689",
            "exclude" to arrayListOf("minutely", "hourly", "daily").joinToString(","),
            "appId" to getString(R.string.open_weather_map_key)
        )

        val call = weatherService.getCurrentWeather(
            "33.441792",
            "-94.037689",
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