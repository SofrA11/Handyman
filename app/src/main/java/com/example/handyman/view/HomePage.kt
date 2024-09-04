package com.example.handyman.view

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.handyman.activity.LokacijaActivity

@Composable
fun HomePage() {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, LokacijaActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Pokreni LokacijaActivity")
    }
}
