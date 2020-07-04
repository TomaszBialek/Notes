package com.example.notes.weather

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.foundations.GPSUtils
import com.example.notes.models.Current
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
import kotlin.collections.ArrayList

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    lateinit var weatherService: WeatherApi
    lateinit var currentLatLng: LatLng

    lateinit var weatherViewModel: WeatherViewModel

    lateinit var adapter: WeatherAdapter


//    val weathersList = arrayListOf<Weather>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", "backPressedMap")

        hourlyWeatherResult.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        currentWeatherResult.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

//        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
//        weatherViewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weather ->
//            weathersList.clear()
//            weathersList.add(weather)
//        })

//        swipeRefresh.setOnRefreshListener {
//            val list = getCurrentWeather()
//            adapter.items.clear()
//            adapter.items.addAll( list )
//            adapter.notifyDataSetChanged()
//            swipeRefresh.isRefreshing = false
//        }
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
//            .addInterceptor(loggingInterceptor)
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
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<Weather>, response: retrofit2.Response<Weather>) {
                if (!response.isSuccessful) {
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

                    val curentList = ArrayList<Current>()
                    curentList.add(current)

                    val currentAdapter = WeatherAdapter(curentList)
                    currentWeatherResult.adapter = currentAdapter

                    val hourly = it.hourly

                    var hourlyText = ""

                    val listName = ArrayList<Current>()

                    for (x in 1 until hourly.size step 3 ) {

                        val hour = hourly[x]

                        val date = Date(hour.dt * 1000)
                        val temp = String.format("%.2f", hour.temp - 273.15)
                        val feels_like = String.format("%.2f", hour.feels_like - 273.15)

                        hourlyText = "Date: ${date.toString().substringBefore("GMT")} -> Temp: $temp (feels like: $feels_like), Humidity: ${hour.humidity}%, Description: ${hour.weather[0].description}"

                        listName.add(hour)
                    }

                    val hourlyAdapter = WeatherAdapter(listName)
                    hourlyWeatherResult.adapter = hourlyAdapter
//                    (hourlyWeatherResult.layoutManager as LinearLayoutManager).stackFromEnd = true

                    val swipeController = SwipeController(requireContext(), hourlyAdapter)
                    val itemTouchhelper = ItemTouchHelper(swipeController)
                    itemTouchhelper.attachToRecyclerView(hourlyWeatherResult)

                    val adapterListener =  object : WeatherAdapter.AdapterListener {
                        override fun startDragListener(viewHolder: RecyclerView.ViewHolder) {
                            itemTouchhelper.startDrag(viewHolder)
                        }
                    }
                    hourlyAdapter.setListener(adapterListener)

                    val tracker: SelectionTracker<Long> = SelectionTracker.Builder<Long>(
                        "selected",
                        hourlyWeatherResult,
                        StableIdKeyProvider(hourlyWeatherResult),
                        MyItemDetailsLookup(hourlyWeatherResult),
                        StorageStrategy.createLongStorage()
                    ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
                        .build()

                    tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                        override fun onSelectionChanged() {
                            val nItems: Int? = tracker?.selection?.size()
                            val colorToolbar: ColorDrawable
                            val titleToolbar: String

                            if(nItems != null && nItems > 0) {
                                titleToolbar = "$nItems items selected"
                                colorToolbar = ColorDrawable(Color.BLUE)
                            } else {
                                titleToolbar = "Nothing selected"
                                colorToolbar = ColorDrawable(getColor(requireContext(), R.color.colorPrimary))
                            }

                            (activity as AppCompatActivity).supportActionBar?.title = titleToolbar
                            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(colorToolbar)
                        }
                    })

                    hourlyAdapter.setTracker(tracker)

                }

            }
        })
    }
}