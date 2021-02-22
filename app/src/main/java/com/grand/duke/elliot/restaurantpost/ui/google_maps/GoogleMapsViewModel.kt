package com.grand.duke.elliot.restaurantpost.ui.google_maps

import android.location.Address
import android.location.Location
import androidx.lifecycle.ViewModel
import com.grand.duke.elliot.restaurantpost.ui.post.location.PlaceContent
import javax.inject.Inject

class GoogleMapsViewModel @Inject constructor(): ViewModel() {

    var countryCode: String? = null
    var currentPlaceContent: PlaceContent? = null
    var lastAddress: Address? = null
    var lastLocation: Location? = null
    var lastLocationTime: Long? = null
}