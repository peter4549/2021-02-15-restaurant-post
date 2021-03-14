package com.grand.duke.elliot.restaurantpost.ui.google_maps

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseActivity
import com.grand.duke.elliot.restaurantpost.databinding.ActivityGoogleMapsBinding
import com.grand.duke.elliot.restaurantpost.ui.place.PlaceEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.post.location.LocationHelper
import com.grand.duke.elliot.restaurantpost.ui.post.location.PlaceContent
import com.grand.duke.elliot.restaurantpost.ui.util.blank
import com.grand.duke.elliot.restaurantpost.ui.util.roundTo
import com.grand.duke.elliot.restaurantpost.ui.util.toSimpleDateFormat
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class GoogleMapsActivity: BaseActivity<GoogleMapsViewModel, ActivityGoogleMapsBinding>(),
        GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
        PlaceEditingDialogFragment.FragmentContainer, PlaceEditingDialogFragment.OnPlaceUpdatedListener {

    override val layoutRes: Int
        get() = R.layout.activity_google_maps

    override fun viewModel(): Class<GoogleMapsViewModel> = GoogleMapsViewModel::class.java

    private object RequestCode {
        const val Autocomplete = 1140
    }

    private val menuRes = R.menu.menu_google_maps_activity

    private val locationHelper = LocationHelper(this)
    private var googleMap: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(viewDataBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.place)

        initMap()

        viewDataBinding.floatingActionButton.setOnClickListener {
            viewModel.lastAddress?.let {
                val monthDate = viewModel.lastLocation?.time
                        ?.toSimpleDateFormat(getString(R.string.pattern_month_date))
                        ?: blank
                val name = "$monthDate ${getString(R.string.last_location)}"
                viewModel.currentPlaceContent = it.toPlaceContent(name)
                updateMapToLastLocation(it, name)
            } ?: run {
                updateLastLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        updateLastLocation()
    }

    override fun onInfoWindowClick(marker: Marker?) {

    }

    private fun updateLastLocation() {
        locationHelper.lastLocation { location ->
            viewModel.lastLocation = location
            viewModel.lastLocationTime = location?.time

            location ?: return@lastLocation
            locationHelper.locationToAddress(location)?.also {
                viewModel.lastAddress = it
                viewModel.countryCode = getCountryCode(it.latitude, it.longitude)

                val monthDate = location.time.toSimpleDateFormat(getString(R.string.pattern_month_date))
                val name = "$monthDate ${getString(R.string.last_location)}"
                viewModel.currentPlaceContent = it.toPlaceContent(name)
                updateMapToLastLocation(it, name)
            }
        }
    }

    private fun startAutocompleteActivity() {
        val fields = listOf(
                Place.Field.ADDRESS,
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME
        )
        
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .apply { viewModel.countryCode?.let { setCountries(listOf(it)) } }
                .build(this)
        startActivityForResult(intent, RequestCode.Autocomplete)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.Autocomplete) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        viewModel.currentPlaceContent = place.toPlaceContent()
                        updateMap(place)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Timber.w("Error: $status")
                    }
                }
                Activity.RESULT_CANCELED -> {}
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getCountryCode(latitude: Double, longitude: Double): String? {
        @Suppress("SpellCheckingInspection")
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        return if (addresses.isNotEmpty())
            addresses[0].countryCode
        else
            null
    }

    private fun initMap() {
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_support_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    private fun updateMapToLastLocation(address: Address, name: String) {
        val latLng = LatLng(address.latitude, address.longitude)

        marker?.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(name)
        markerOptions.snippet(address.getAddressLine(0))
        markerOptions.draggable(true)

        marker = googleMap?.addMarker(markerOptions)

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15F)
        googleMap?.moveCamera(cameraUpdate)
    }

    private fun updateMap(place: Place) {
        val name = place.name
        val address = place.address
        val latLng = place.latLng ?: return

        marker?.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(name)
        markerOptions.snippet(address)
        markerOptions.draggable(true)
        marker = googleMap?.addMarker(markerOptions)

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15F)
        googleMap?.moveCamera(cameraUpdate)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(menuRes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.item_search -> {
                startAutocompleteActivity()
                true
            }
            R.id.item_save -> {
                viewModel.currentPlaceContent?.also {
                    showToast("PPP: $it")
                    showPlaceCreatingDialog(it)
                } ?: run {
                    showToast(getString(R.string.could_not_load_your_locations))
                }
                true
            }
            else -> false
        }
    }

    private fun showPlaceCreatingDialog(placeContent: PlaceContent) {
        val title = getString(R.string.create_place)

        PlaceEditingDialogFragment().apply {
            setTitle(title)
            setPlaceContent(placeContent)
            show(supportFragmentManager, tag)
        }
    }

    private fun setupColor() {

    }

    private fun Address.toPlaceContent(name: String) = PlaceContent(
            name = name,
            latitude = this.latitude.roundTo(4),
            longitude = this.longitude.roundTo(4)
    )

    private fun Place.toPlaceContent(): PlaceContent? {
        val name = name ?: return null
        val latitude = latLng?.latitude ?: return null
        val longitude = latLng?.longitude ?: return null

        return PlaceContent(
                name = name,
                latitude = latitude.roundTo(4),
                longitude = longitude.roundTo(4)
        )
    }

    /** PlaceEditingDialogFragment.OnPlaceUpdatedListener. */
    override fun onError(throwable: Throwable) {
        Timber.e(throwable)
        showToast(getString(R.string.failed_to_save_place))
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onPlaceInserted(place: com.grand.duke.elliot.restaurantpost.persistence.data.Place?) {
        finish()
    }

    override fun onPlaceUpdated(place: com.grand.duke.elliot.restaurantpost.persistence.data.Place) {
        /** Nothing to do. */
    }

    /** PlaceEditingDialogFragment.FragmentContainer. */
    override fun onRequestOnPlaceUpdatedListener(): PlaceEditingDialogFragment.OnPlaceUpdatedListener = this
}