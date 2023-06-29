package com.azalia.submission1_storyapp.ui.map

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.azalia.submission1_storyapp.R
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.databinding.ActivityMapsBinding
import com.azalia.submission1_storyapp.getAddressName
import com.azalia.submission1_storyapp.response.StoryResponse
import com.azalia.submission1_storyapp.ui.ViewModelFactory
import com.azalia.submission1_storyapp.util.Constanta.EXTRA_TOKEN
import com.azalia.submission1_storyapp.util.ViewStateCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity() : AppCompatActivity(), OnMapReadyCallback, ViewStateCallback<StoryResponse> {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mapsViewModel: MapsViewModel by viewModels {
        factory
    }
    private var token: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = intent.getStringExtra(EXTRA_TOKEN)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        token?.let { it2 ->
            mapsViewModel.getStoriesLocation(it2).observe(this) {result ->
                Log.e(TAG, "$result")
                when(result) {
                    is Resource.Success -> result.data?.let { it1 -> onSuccess(it1) }
                    is Resource.Failure -> onFailed(result.message)
                    is Resource.Loading -> onLoading()
                }
            }
        }

        mapsViewModel.coordinateTemp.observe(this) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 4f))
        }

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    mapsViewModel.coordinateTemp.postValue(
                        LatLng(it.latitude, it.longitude)
                    )
                } else {
                    mapsViewModel.coordinateTemp.postValue(LatLng(-6.8957643, 107.6338462))
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_stylel))
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()
    override fun onSuccess(data: StoryResponse) {
        Log.e(TAG, "onSuccess Map: $data")
        val list = data.listStory
        list.forEach{story ->
            val latLng = LatLng(story.lat!!, story.lon!!)
            val addressName = getAddressName(this, story.lat, story.lon)
            mMap.addMarker(MarkerOptions().position(latLng).title("by ${story.name} : ${story.description}").snippet("$addressName"))
            boundsBuilder.include(latLng)
        }
    }

    override fun onLoading() {
        Log.e(TAG,"onLoading Maps")
    }

    override fun onFailed(message: String?) {
        Log.e(TAG, "onFailed Maps: $message")
    }

}