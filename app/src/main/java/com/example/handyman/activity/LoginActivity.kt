package com.example.handyman.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.example.handyman.MainActivity
import com.example.handyman.data.UserSession
import com.example.handyman.ui.theme.HandyManTheme
import com.example.handyman.view.LoginForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {  // Ovo znači da je korisnik već prijavljen u aplikaciju.

            val uid = currentUser.uid
            loadUserFromDatabase(uid)
        } else {
            setContent {
                HandyManTheme {
                    LoginForm()
                }
            }
        }
        /*
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Zatvaranje LoginActivity nakon preusmeravanja na MainActivity*/
    }
    private fun loadUserFromDatabase(uid: String) {
        val database = FirebaseDatabase.getInstance("https://handyman-a2aa1-default-rtdb.europe-west1.firebasedatabase.app").reference.child("users").child(uid)

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Popuni UserSession sa podacima
                UserSession.mejl = snapshot.child("mejl").getValue(String::class.java)
                UserSession.ime = snapshot.child("ime").getValue(String::class.java)
                UserSession.prezime = snapshot.child("prezime").getValue(String::class.java)
                UserSession.korisnickoIme = snapshot.child("korisnickoIme").getValue(String::class.java)
                UserSession.brojTelefona = snapshot.child("brojTelefona").getValue(String::class.java)
                UserSession.imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                UserSession.uid = uid

                Toast.makeText(this, "Welcome ${UserSession.ime}", Toast.LENGTH_SHORT).show()

                // Preusmeri na MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Zatvori LoginActivity
            } else {
                Log.e("LoginActivity", "No user data found for uid: $uid")
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("LoginActivity", "Failed to retrieve user data for uid: $uid", exception)
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
        }
    }
}
