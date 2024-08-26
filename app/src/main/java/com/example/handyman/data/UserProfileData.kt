package com.example.handyman.data

import android.net.Uri
import com.google.firebase.database.Exclude

import com.google.firebase.database.IgnoreExtraProperties
@IgnoreExtraProperties
data class UserProfileData(
        var mejl: String = "",
        var pwd: String = "",
        var ime: String = "",
        var prezime: String = "",
        var korisnickoIme: String = "",
        var brojTelefona: String = "",
        var fotoUri: Uri = Uri.EMPTY,
        var remember: Boolean = false,
        var uid:String = "",
        var pictureUrl:String =""
) {     @Exclude
        fun isNotEmpty(): Boolean {
                return mejl.isNotEmpty() && pwd.isNotEmpty() && ime.isNotEmpty() && prezime.isNotEmpty() &&
                        korisnickoIme.isNotEmpty() && brojTelefona.isNotEmpty()
        }
        @Exclude
        fun loginIsNotEmpty():Boolean{
                return mejl.isNotEmpty() && pwd.isNotEmpty()
        }
}
