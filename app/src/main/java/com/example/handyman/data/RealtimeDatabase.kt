package com.example.handyman.data

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

data class RealtimeDatabase(val user:UserProfileData, val reference: DatabaseReference){
    var database = FirebaseDatabase.getInstance().getReference("Users")
    var storage = FirebaseStorage.getInstance().getReference("Users/"+user.mejl)


     suspend fun SacuvajKorisnika(user:UserProfileData, reference: DatabaseReference){
        database.child("Users").push().setValue(user).await()
       // storage.putFile(user.fotoUri).await()
    }

    suspend fun pribaviKorisnika(email: String): UserProfileData? {
        val snapshot = database.child("Users")
            .orderByChild("email")
            .equalTo(email)
            .limitToFirst(1)
            .get()
            .await()

        return snapshot.children.firstOrNull()?.getValue(UserProfileData::class.java)
    }

    suspend fun azurirajKorisnika(email: String, noviPodaci: UserProfileData): Boolean {
        val snapshot = database.child("Users")
            .orderByChild("email")
            .equalTo(email)
            .limitToFirst(1)
            .get()
            .await()

        val korisnikSnapshot = snapshot.children.firstOrNull()

        return if (korisnikSnapshot != null) {
            korisnikSnapshot.ref.setValue(noviPodaci).await()
            true
        } else {
            false
        }
    }
    /*
    Kreiraj mapu sa poljima koja želiš da ažuriraš
    val updateMap = mapOf(
    "ime" to "Novo Ime",
    "prezime" to "Novo Prezime",
    "godine" to 25)
    */
    suspend fun azurirajKorisnikaDelimicno(email: String, updateMap: Map<String, Any?>): Boolean {
        val snapshot = database.child("Users")
            .orderByChild("email")
            .equalTo(email)
            .limitToFirst(1)
            .get()
            .await()

        val korisnikSnapshot = snapshot.children.firstOrNull()

        return if (korisnikSnapshot != null) {
            korisnikSnapshot.ref.updateChildren(updateMap).await()
            true
        } else {
            false
        }
    }



}
