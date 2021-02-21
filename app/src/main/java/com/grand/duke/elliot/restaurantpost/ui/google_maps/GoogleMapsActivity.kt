package com.grand.duke.elliot.restaurantpost.ui.google_maps

import android.location.Address
import android.os.Bundle
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseActivity
import com.grand.duke.elliot.restaurantpost.databinding.ActivityGoogleMapsBinding
import com.grand.duke.elliot.restaurantpost.ui.post.location.LocationHelper
import timber.log.Timber

class GoogleMapsActivity: BaseActivity<GoogleMapsViewModel, ActivityGoogleMapsBinding>(),
    OnMapReadyCallback {
    override val layoutRes: Int
        get() = R.layout.activity_google_maps

    override fun viewModel(): Class<GoogleMapsViewModel> = GoogleMapsViewModel::class.java

    private val locationHelper = LocationHelper(this)
    private var googleMap: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMap()
        initAutocompleteFragment()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        locationHelper.lastLocation { location ->
            showToast("dddddd $location")
            location ?: return@lastLocation
            locationHelper.locationToAddress(location)?.also { updateMap(it) }
        }
    }

    private fun initAutocompleteFragment() {
        val autocompleteSupportFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_container_view)
                    as AutocompleteSupportFragment

        autocompleteSupportFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

            }

            override fun onError(status: Status) {
                Timber.w("Error: $status")
            }
        })
    }

    private fun initMap() {
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_support_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    private fun updateMap(address: Address) {
        val name = address.featureName
        val latLng = LatLng(address.latitude, address.longitude)

        marker?.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(name)
        markerOptions.draggable(true)
        marker = googleMap?.addMarker(markerOptions)

        showToast("????: $address")

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15F)
        googleMap?.moveCamera(cameraUpdate)
    }
}