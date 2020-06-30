package com.example.notes.weather

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.models.Current
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_weather.view.*
import java.util.*

class WeatherAdapter(private val items: List<Current>) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>(){

    companion object {
        private const val ICON_URL = "http://openweathermap.org/img/w/"
        private const val ICON_EXTENSION = ".png"
        private const val CELSIUS_CHAR = "\u2103"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        val weatherViewHolder = WeatherViewHolder(view)

        view.setOnClickListener { Log.d("WeatherAdapter", weatherViewHolder.adapterPosition.toString()) }

        return weatherViewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = String.format("%.2f %s", item.temp - 273.15, CELSIUS_CHAR)

        val date = Date(item.dt * 1000)
        holder.city.text = "Date: ${date.toString().substringBefore("GMT")}"

        var icon = item.weather[0].icon
        icon = ICON_URL + icon + ICON_EXTENSION

        Picasso.get().load(icon).into(holder.color)
    }

    inner class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.name
        val city: TextView = view.city
        val color: ImageView = view.color
    }
}