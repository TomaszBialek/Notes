package com.example.notes.foundations

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.notes.R
import com.google.android.gms.maps.model.LatLng

class GPSUtils {

    private var locationManager: LocationManager? = null
    var latitude = 0.0
    var longitude = 0.0

    fun initPermissions(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION
        )
    }

    fun findDeviceLocation(activity: Activity) {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnable(activity)
        } else {
            getLocation(activity)
        }
    }

    private fun getLocation(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        } else {

            val locationGps = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val locationNetwork = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val locationPassive = locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

            when {
                locationGps != null -> {
                    latitude = locationGps.latitude
                    longitude = locationGps.longitude
                }
                locationNetwork != null -> {
                    latitude = locationNetwork.latitude
                    longitude = locationNetwork.longitude
                }
                locationPassive != null -> {
                    latitude = locationPassive.latitude
                    longitude = locationPassive.longitude
                }
                else -> {
                    Toast.makeText(activity, activity.getString(R.string.toast_error_location), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun gpsEnable(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
            .setMessage("Enable GPS")
            .setCancelable(false)
            .setPositiveButton("YES") { dialog, which -> activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("NO") { dialog, which -> dialog.cancel() }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    val latLng: LatLng
        get() = LatLng(latitude, longitude)

    companion object {
        const val REQUEST_LOCATION = 1
        val instance = GPSUtils()
    }
}