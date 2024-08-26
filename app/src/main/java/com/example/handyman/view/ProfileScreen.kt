package com.example.handyman.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.handyman.data.UserSession

@Composable
fun ProfileScreen() {
    val userSession = UserSession

    // Define the image painter and other values
    val profileImagePainter = rememberAsyncImagePainter(model = userSession.imageUrl)
    val fullName = "${userSession.ime} ${userSession.prezime}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile Image
        Card(
            modifier = Modifier.size(120.dp).
            clip(CircleShape),
        ) {
            Image(
                painter = profileImagePainter,
                contentDescription = "Profile Image",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Full Name
        Text(
            text = fullName,
            style = androidx.compose.material3.Typography().headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        Text(
            text = userSession.mejl ?: "No email provided",
            style = androidx.compose.material3.Typography().bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Other details
        Text(
            text = "Username: ${userSession.korisnickoIme ?: "N/A"}",
            style = androidx.compose.material3.Typography().bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Phone: ${userSession.brojTelefona ?: "N/A"}",
            style = androidx.compose.material3.Typography().bodyLarge
        )
    }
}
