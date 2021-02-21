package com.grand.duke.elliot.restaurantpost.ui.post.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.ui.util.roundTo
import java.util.*

class LocationHelper(private val activity: Context) {

    fun lastLocation(onLastLocation: (Location?) -> Unit) {
        val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    onLastLocation(location)
                }
    }

    fun locationToAddress(location: Location): Address? {
        val addresses = Geocoder(activity, Locale.getDefault())
            .getFromLocation(
                location.latitude.roundTo(7),
                location.longitude.roundTo(7),
                1
            ) ?: run {
            return null
        }

        if (addresses.isEmpty())
            return null

        return addresses[0]
    }

    fun defaultPlaceContent() = PlaceContent(
            name = activity.getString(R.string.default_place_name),
            latitude = 37.5642135,
            longitude = 127.0016985
    )
}

data class PlaceContent(
        val name: String,
        val latitude: Double,
        val longitude: Double
)