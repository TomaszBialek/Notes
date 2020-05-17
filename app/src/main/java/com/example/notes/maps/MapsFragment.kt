package com.example.notes.maps

import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.application.NoteApplication
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException


class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private lateinit var placesClient: PlacesClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude), true)
            }
        }

        createLocationRequest()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_map_type, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.map_type_none -> {
                map.mapType = GoogleMap.MAP_TYPE_NONE
            }
            R.id.map_type_normal -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            R.id.map_type_satellite -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            R.id.map_type_terrain -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            R.id.map_type_hybrid -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", "backPressedMap")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setCountry("PL")
        autocompleteFragment.setPlaceFields(mutableListOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG))

        childFragmentManager.beginTransaction().apply {
            hide(autocompleteFragment)
            commit()
        }

        if (!Places.isInitialized()) {
            Places.initialize(NoteApplication.instance.applicationContext, getString(R.string.google_maps_key))
        }

        placesClient = Places.createClient(requireContext())

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let {
                    placeMarkerOnMap(it, false)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15.0f))
                }
                Log.i(CLASS_NAME, "Place: " + place.name + ", " + place.id)
            }

            override fun onError(p0: Status) {
                Log.i(CLASS_NAME, "An error occurred: $p0")
            }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            loadPlacePicker()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            setOnMarkerClickListener(this@MapsFragment)
            isMyLocationEnabled = true
            isIndoorEnabled = true
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng, true)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = 10_000
            fastestInterval = 5_000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireContext())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationUpdateState = true
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        locationUpdateState = false
    }

    override fun onResume() {
        super.onResume()

        if (!locationUpdateState) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun getAddress(latLng: LatLng): String {
        val addressText = StringBuilder()

        try {
            val addresses = Geocoder(context).getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex + 1) {
                    if (i != 0) addressText.append("\n")
                    addressText.append(address.getAddressLine(i))
                }
            }
        } catch (e: IOException) {
            Log.e(CLASS_NAME, e.localizedMessage)
        }

        return addressText.toString()
    }


    private fun placeMarkerOnMap(location: LatLng, isUser: Boolean) {

        val markerOptions =
            if (isUser) {
                MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            } else {
                MarkerOptions()
            }.apply {
                position(location)
                val titleAddress = getAddress(location)
                title(titleAddress)
            }

        map.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    private fun loadPlacePicker() {
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)

        if (autocompleteFragment != null) {
            if (autocompleteFragment.isHidden) {
                childFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(autocompleteFragment)
                    .commit()
            } else {
                childFragmentManager
                    .beginTransaction()
                    .hide(autocompleteFragment)
                    .commit()
            }
        }
    }

    companion object {
        const val CLASS_NAME = "MapsFragment"
        const val REQUEST_CHECK_SETTINGS = 2
    }

}