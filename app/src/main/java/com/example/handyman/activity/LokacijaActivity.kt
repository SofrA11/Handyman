package com.example.handyman.activity


import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handyman.R
import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.handyman.view.HomePage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.handyman.view.MapPage
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
class LokacijaActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude = mutableStateOf<Double?>(null)
    private var longitude = mutableStateOf<Double?>(null)
    private var isLoading = mutableStateOf(true) // Dodaj stanje učitavanja

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissions() // Pokreće dobijanje lokacije

        setContent {
            // Čeka se dok se ne dobiju latitude i longitude vrednosti
            val lat = latitude.value
            val long = longitude.value
            val loading = isLoading.value
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else if (lat != null && long != null) {
                MapPage()
            } else {
                Text("Učitavanje lokacije...")
            }
            Button(
                onClick = { finish() }, // Završava aktivnost i vraća se na prethodnu
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Nazad na glavnu stranu")
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getLastKnownLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getLastKnownLocation()
            }
            else -> {
                Toast.makeText(this, "Lokacione dozvole nisu odobrene", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                latitude.value = it.latitude
                longitude.value = it.longitude
                isLoading.value = false
            } ?: run {
                Toast.makeText(this, "Nije moguće dobiti lokaciju", Toast.LENGTH_SHORT).show()
            }
        }
    }
}