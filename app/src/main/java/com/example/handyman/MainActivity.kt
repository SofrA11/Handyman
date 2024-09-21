package com.example.handyman

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.handyman.components.MainNavigation
import com.example.handyman.service.LocationService
import com.example.handyman.ui.theme.HandyManTheme
import com.example.handyman.view.HomePage
import android.Manifest
import com.example.handyman.data.UserSession

class MainActivity : ComponentActivity() {
    private val REQUEST_LOCATION_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Provera dozvola prilikom pokretanja aktivnosti
        if (checkLocationPermission()) {
            startLocationService()
        } else {
            requestLocationPermission()
        }
        setContent {
            HandyManTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }

    // Provera dozvola
    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Zahtev za dozvolu
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    // Obrada rezultata dozvole
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationService()
        }
    }

    // Startovanje servisa
    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        startService(intent)
    }
}
