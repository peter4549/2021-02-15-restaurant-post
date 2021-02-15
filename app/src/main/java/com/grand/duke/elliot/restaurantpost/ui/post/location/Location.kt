package com.grand.duke.elliot.restaurantpost.ui.post.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import java.util.*

class Location(private val activity: Activity) {

    fun lastPlace(onLastPlace: (Place?) -> Unit) {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                onLastPlace(location?.toPlace())
            }
    }

    private fun Location.toPlace(): Place? {
        val addresses = Geocoder(activity, Locale.getDefault()).getFromLocation(
            latitude,
            longitude,
            1
        ) ?: run {
            return null
        }

        if (addresses.isEmpty())
            return null

        val address = addresses[0]
        return Place(
            name = address.getAddressLine(0),
            latitude = address.latitude,
            longitude = address.longitude
        )
    }

    fun defaultPlace() = Place(
        name = activity.getString(R.string.default_place_name),
        latitude = 37.5642135,
        longitude = 127.0016985
    )

    companion object {

    }
}