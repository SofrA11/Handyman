package com.example.handyman

import android.app.Application
import com.google.firebase.FirebaseApp

class HandyManApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}