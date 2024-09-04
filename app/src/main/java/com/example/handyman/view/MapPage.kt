package com.example.handyman.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage() {
    val context = LocalContext.current

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val permissions = remember { mutableStateOf(false) } // Početno stanje je bez dozvola

    // Proveri da li su dozvole dodeljene
    val permissionCheck = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        permissions.value = true
    } else {
        // Logika za zahtevanje dozvola može se dodati ovde
    }

    val cameraPositionState = rememberCameraPositionState {
        // Inicijalizuj sa nekom početnom lokacijom i zoom-om
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
    }
    var userLocation : LatLng
    LaunchedEffect(Unit) {
        if (permissions.value) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, 15f)
                }
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = true,
            myLocationButtonEnabled = true, // Omogućavanje dugmeta za lokaciju
            compassEnabled = true
        ),
        properties = MapProperties(
            isMyLocationEnabled = permissions.value // Omogućavanje prikazivanja trenutne lokacije na mapi
        )
    ) { }
}
