package com.example.notes.weather

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.models.Current
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_weather.view.*
import java.util.*

class WeatherAdapter(val items: List<Current>) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    companion object {
        private const val ICON_URL = "http://openweathermap.org/img/w/"
        private const val ICON_EXTENSION = ".png"
        private const val CELSIUS_CHAR = "\u2103"
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private var tracker: SelectionTracker<Long>? = null

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    private var listener: AdapterListener? = null

    interface AdapterListener {
        fun startDragListener(viewHolder: RecyclerView.ViewHolder)
    }

    fun setListener(AdapterListener: AdapterListener) {
        listener = AdapterListener
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

        tracker?.let {
            holder.parent.background = if (it.isSelected(position.toLong())) ColorDrawable(Color.GRAY) else ColorDrawable(Color.TRANSPARENT)
        }
    }

    inner class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.name
        val city: TextView = view.city
        val color: ImageView = view.color
        val parent: View = view.cv

        init {
            color.setOnClickListener { Log.d("TAG icon", items[layoutPosition].weather[0].icon) }

            view.dragHandle.setOnTouchListener { view, event ->
                view.performClick()
                if (event.action == MotionEvent.ACTION_DOWN) {
                    listener?.startDragListener(this)
                }
                false
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }
}

class MyDiffCallback(
    private val oldList: ArrayList<Current>,
    private val newList: ArrayList<Current>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].dt == newList[newItemPosition].dt
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].temp == newList[newItemPosition].temp
    }
}