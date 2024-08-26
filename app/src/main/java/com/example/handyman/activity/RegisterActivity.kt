package com.example.handyman.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.handyman.ui.theme.HandyManTheme
import com.example.handyman.view.LoginForm
import com.example.handyman.view.RegisterForm
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : ComponentActivity() {
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent{
            HandyManTheme {
                RegisterForm()
            }
        }
    }
}