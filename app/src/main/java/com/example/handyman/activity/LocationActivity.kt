package com.example.handyman.activity

import MapScreen
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handyman.R
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.api.IMapController

class LocationActivity : AppCompatActivity() {
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var mapController: IMapController
    private lateinit var mMap: MapView

    private var currentPoint: GeoPoint? = null
    private var latLngText by mutableStateOf("lat - lng")

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requestLocationUpdate()
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            this,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        mMap = MapView(this).apply {
            setMultiTouchControls(true)
        }

        initializeOSM()

        setContent {
            MapScreen(
                mapView = mMap,
                latLngText = latLngText,
                onCurrentLocationClick = { requestLocationUpdate() }
            )
        }
    }

    private fun initializeOSM() {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        mapController = mMap.controller

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestLocationUpdate()
        }
    }

    private fun requestLocationUpdate() {
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true

        myLocationOverlay.runOnFirstFix {
            GlobalScope.launch(Dispatchers.Main) {
                currentPoint = myLocationOverlay.myLocation
                mapController.setCenter(currentPoint)
                mapController.animateTo(currentPoint)

                mMap.overlays.add(myLocationOverlay)
                mapController.setZoom(19.0)

                updateTextView(currentPoint!!)
            }
        }
    }

    private fun updateTextView(geoPoint: GeoPoint) {
        val latitude = geoPoint.latitude
        val longitude = geoPoint.longitude
        latLngText = "Latitude: $latitude, Longitude: $longitude"
    }
}