package com.example.handyman.data

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng

object UserSession {
    var mejl: String?=null
    var ime: String?=null
    var prezime: String?=null
    var korisnickoIme: String?=null
    var brojTelefona: String?=null
    var imageUrl: String?=null
    var uid:String?=null
    var location:LatLng?=null
    var poslovi: MutableList<JobData>? = null
    fun clearSession() {
        mejl = null
        ime = null
        prezime = null
        korisnickoIme = null
        brojTelefona = null
        imageUrl = null
        uid = null
        location=null
    }
    fun saveUserSession(context: Context, isLoggedIn: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", isLoggedIn)
            apply()
        }
    }
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun logoutUser(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("is_logged_in")
            apply()
        }
    }


}