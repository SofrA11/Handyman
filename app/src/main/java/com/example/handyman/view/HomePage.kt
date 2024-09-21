package com.example.handyman.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.handyman.activity.LokacijaActivity
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.rememberAsyncImagePainter
import com.example.handyman.activity.JobActivity
import com.example.handyman.components.JobForm
import com.example.handyman.data.JobData
import com.example.handyman.data.UserSession
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Calendar
import java.util.Date
import com.google.firebase.firestore.ktx.firestore
import kotlin.math.*


import android.icu.text.SimpleDateFormat
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.tasks.await

import java.util.Locale
@Composable
fun HomePage() {
/*
    val cameraPositionState = rememberCameraPositionState {
        // Inicijalizuj sa nekom početnom lokacijom i zoom-om
        position = CameraPosition.fromLatLngZoom(UserSession.location!!, 15f)
    }*/

    val context: Context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Spacer(modifier = Modifier.height(64.dp))
            Button(
                onClick = {
                    val intent = Intent(context, JobActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RectangleShape
            ) {
                Text("Додај оглас")
            }

            Spacer(modifier = Modifier.height(16.dp))

            JobFilter()


            Spacer(modifier = Modifier.height(16.dp))

            GoogleMap(
                modifier = Modifier.size(300.dp).fillMaxSize(),
               // cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    scrollGesturesEnabled = true,
                    zoomGesturesEnabled = true,
                    tiltGesturesEnabled = true,
                    myLocationButtonEnabled = true, // Omogućavanje dugmeta za lokaciju
                    compassEnabled = true
                )
            ) { }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, LokacijaActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)

            ) {
                Text("Otvori MapaActivity")
            }
    }
}

@Composable
fun CheckboxFilter(label: String) {
    var checked by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Text(label)
    }
}

fun filter(
    korisnickoIme: String?,
    vrstaUsluge: String?,
    cena: Double,
    startDate: Date?,
){
    // Pristupi podacima o poslovima u Realtime Database
    val database = FirebaseDatabase.getInstance("https://handyman-a2aa1-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("Jobs")

    database.get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {

            // Očisti trenutne poslove u UserSession pre popunjavanja novih podataka
            UserSession.poslovi = mutableListOf()

            for (jobSnapshot in snapshot.children) {

                var dodaj: Boolean = false;
                var nemaFiltera: Boolean = true;
                // Pretpostavimo da koristiš JobData kao klasu za posao
                val job = jobSnapshot.getValue(JobData::class.java)

                if (job != null) {
                    println("AAA")
                    if(!korisnickoIme.isNullOrEmpty()){
                        println("Korisnicko Ime")
                        if(job.korisnickoIme==korisnickoIme)
                        {
                            println("Korisnicko Ime: ${job.korisnickoIme}")
                            dodaj=true;
                            nemaFiltera = false;
                        }
                        else{
                            println("Korisnicko Ime: continue")
                            continue;
                        }
                    }
                    if(!vrstaUsluge.isNullOrEmpty())
                    { println("vrstaUsluge")
                        if(job.vrstaUsluge==vrstaUsluge)
                        {
                            println("vrstaUsluge: ${job.vrstaUsluge}")
                            dodaj=true;
                            nemaFiltera = false;
                        }
                        else{
                            println("vrstaUsluge: continue")
                            continue;
                        }
                    }
                   if(cena>0.0){
                       println("Cena")
                       if(job.cena<=cena)
                       {
                           println("cena: ${job.cena}")
                           dodaj=true;
                           nemaFiltera = false;
                       }
                       else{
                           println("cena: continue")
                           continue;
                       }
                   }
                   if(startDate!=null){
                       println("startDate")
                       if(job.createdDate.after(startDate))
                       {
                           println("startDate: ${job.createdDate}")
                           dodaj=true;
                           nemaFiltera = false;
                       }
                       else{
                           println("startDate: continue")
                           continue;
                       }
                   }
                    println("Dodaj: ${dodaj}")
                    println("Nema filtera: ${nemaFiltera}")
                   if(dodaj==true){
                       UserSession.poslovi?.add(job)
                   }else{
                       if(nemaFiltera==true){//Ako nema filtera, dodaj svaki posao
                           UserSession.poslovi?.add(job)
                       }
                   }
                }
            }
            // Sada možeš koristiti listu poslova iz UserSession
           println("Successfully retrieved ${UserSession.poslovi?.size} jobs")

        } else {
            println("No job data found")

        }
    }.addOnFailureListener {
        println("Failed to retrieve job data ${it}")

    }

}
fun filterJobsFirestore(
    korisnickoIme: String?,
    vrstaUsluge: String?,
    cena: Double,
    startDate: Date?,
    context: Context
) {
    println("Usao u filterJobsFirestore")
    val firestore = FirebaseFirestore.getInstance()
    val jobsRef = firestore.collection("poslovi")

    jobsRef.get()
        .addOnSuccessListener { snapshot ->
            val jobs = snapshot.documents.mapNotNull { document ->
                document.toObject(JobData::class.java)
            }
            // Ispisivanje podataka
            for (job in jobs) {
                if (UserSession.poslovi == null) {
                    UserSession.poslovi = mutableListOf()
                }
                // Postavi flag koji će označiti da li posao treba biti dodat u listu
                var shouldAdd = true

                // Filtriraj po korisničkom imenu ako je prosleđeno
                if (korisnickoIme != null && job.korisnickoIme != korisnickoIme) {
                    shouldAdd = false
                    println("Usao u filterJobsFirestore")
                }

                // Filtriraj po vrsti usluge ako je prosleđena
                if (vrstaUsluge != null && job.vrstaUsluge != vrstaUsluge) {
                    shouldAdd = false
                }

                // Filtriraj po ceni ako je prosleđena (na primer, ako je cena <= prosleđene vrednosti)
                if (cena > 0 && job.cena > cena) {
                    shouldAdd = false
                }

                // Filtriraj po datumu (na primer, poslovi koji su kasniji od startDate)
                if (startDate != null && job.createdDate != null && job.createdDate.before(startDate)) {
                    shouldAdd = false
                }

                // Ako su svi uslovi ispunjeni, dodaj posao u listu
                if (shouldAdd) {
                    println("Add ${job.jobid}")
                    UserSession.poslovi?.add(job)
                }
            }
        }
        .addOnFailureListener { exception ->
            // Obradi grešku ako je potrebno
            println("Došlo je do greške: ${exception.message}")
        }
}
fun filterJobsFirestore2(
    korisnickoIme: String?,
    vrstaUsluge: String?,
    cena: Double?,
    startDate: Date?,
    context: Context
) {
    println("Usao u filterJobsFirestore")
    val firestore = FirebaseFirestore.getInstance()
    var jobsRef:Query = firestore.collection("jobs") // Bazni referentni upit bez polja "aktivno"

    // Filtriraj po korisnickom imenu ako je prosleđeno
    if (korisnickoIme != null) {
        jobsRef = jobsRef.whereEqualTo("korisnickoIme", korisnickoIme)
    }
    println("korisnickoIme: ${jobsRef}")
    // Filtriraj po vrsti usluge ako je prosleđena
    if (vrstaUsluge != null) {
        jobsRef = jobsRef.whereEqualTo("vrstaUsluge", vrstaUsluge)
    }

    // Filtriraj po ceni ako je prosleđena
    if (cena != null) {
        jobsRef = jobsRef.whereLessThanOrEqualTo("cena", cena)
    }

    // Filtriraj po datumu ako je prosleđen
    if (startDate != null) {
        jobsRef = jobsRef.whereGreaterThanOrEqualTo("datumPocetka", startDate)
    }

    jobsRef.get()
        .addOnSuccessListener { snapshot ->
            val jobs = snapshot.documents.mapNotNull { document ->
                document.toObject(JobData::class.java)
            }

            // Ispisivanje podataka u UserSession
            if (UserSession.poslovi == null) {
                UserSession.poslovi = mutableListOf()
            }
            for (job in jobs) {
                UserSession.poslovi?.add(job)
            }

            // Dodatni output ili update UI
            println("Broj pronađenih poslova: ${jobs.size}")
        }
        .addOnFailureListener { exception ->
            // Obradi grešku ako je potrebno
            println("Došlo je do greške: ${exception.message}")
        }
}


@Composable
fun JobFilter() {
    var korisnickoIme by remember { mutableStateOf("") }
    var vrstaUsluge by remember { mutableStateOf("") }
    var cena by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Date?>(null) }
    val context = LocalContext.current
    Column {
        Text("Unesite podatke o poslu")
        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = korisnickoIme,
            onValueChange = { korisnickoIme = it },
            label = { Text("Корисничко име") },
            modifier = Modifier
                .fillMaxWidth()
                .width(200.dp) // Postavi željenu širinu
        )

        OutlinedTextField(
            value = vrstaUsluge,
            onValueChange = { vrstaUsluge = it },
            label = { Text("Врста услуге") },
            modifier = Modifier
                .fillMaxWidth()
                .width(200.dp) // Postavi željenu širinu
        )

        OutlinedTextField(
            value = cena,
            onValueChange = { cena = it },
            label = { Text("Цена") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .width(200.dp) // Postavi željenu širinu
        )

        DatePickerField(
            label = "Датум постављања огласа",
            onDateSelected = { startDate = it }
        )

        Button(
            onClick = {
                var updateCena:Double=0.0;
                if(!cena.isNullOrEmpty()){
                    updateCena = cena.toDouble();
                }
                //filterJobsFirestore2(korisnickoIme=korisnickoIme,vrstaUsluge=vrstaUsluge,cena=updateCena, startDate = startDate, context = context)
               //filterJobsFirestore(korisnickoIme=korisnickoIme,vrstaUsluge=vrstaUsluge,cena=updateCena, startDate = startDate, context = context);
                filter(korisnickoIme=korisnickoIme,vrstaUsluge=vrstaUsluge,cena=updateCena, startDate = startDate)
            },
            modifier = Modifier
                .padding(vertical = 8.dp)

        ) {
            Text(" Филтрирај")
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    onDateSelected: (Date) -> Unit // Funkcija za prosleđivanje odabranog datuma
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Čuvanje prikaza odabranog datuma
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    // Definiši format za datum
    val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    } else {
        SimpleDateFormat("dd.MM.yyyy")
    }

    // Inicijalizuj DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Kreiraj novi Date objekat
            val date = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time

            // Postavi odabrani datum i pozovi callback
            selectedDate = date
            onDateSelected(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // UI deo za prikaz polja
    Column {
        OutlinedTextField(
            value = selectedDate?.let { dateFormat.format(it) } ?: "",
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .width(200.dp),
            readOnly = true, // Onemogući ručni unos
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Izaberite datum")
                }
            }
        )
    }
}
data class Location(val latitude: Double, val longitude: Double)

fun calculateDistance(userLocation: Location, job:JobData): Double {
    println("Racunam distancu")
    val R = 6371e3 // Радијус Земље у метрима
    val φ1 = Math.toRadians(userLocation.latitude)
    val φ2 = Math.toRadians(job.latitude)
    val Δφ = Math.toRadians(job.latitude - userLocation.latitude)
    val Δλ = Math.toRadians(job.longitude - userLocation.longitude)

    val a = sin(Δφ / 2) * sin(Δφ / 2) +
            cos(φ1) * cos(φ2) *
            sin(Δλ / 2) * sin(Δλ / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = R * c // Раздаљина у метрима
    return distance
}

fun filterMarkers(userLocation: Location, markers: List<JobData>, radius: Double): List<JobData> {
    println("Filtriram")
    return markers.filter { marker ->
        calculateDistance(userLocation, marker) <= radius
    }
}

data class Marker( var location:Location,
                   var job:JobData,
){}

fun getMarkers(jobs: List<JobData>): List<JobData>{
    println("Usao u getMarker ${jobs.count()}")
    val userLocation = UserSession.location.let { Location(it!!.latitude, it.longitude) }

    for(data in jobs){
        println("Distanca: ${data.jobid}")
    }
    // Филтрирај маркере унутар 1000 метара од корисника
    val filteredMarkers = filterMarkers(userLocation, jobs, 1000.0)

    return  filteredMarkers;
}