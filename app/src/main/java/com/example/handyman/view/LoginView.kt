package com.example.handyman.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handyman.MainActivity
import com.example.handyman.activity.LoginActivity
import com.example.handyman.activity.RegisterActivity
import com.example.handyman.components.MainNavigation
import com.example.handyman.data.UserProfileData
import com.example.handyman.ui.theme.HandyManTheme
import com.google.firebase.auth.FirebaseAuth
import com.example.handyman.data.UserSession
import com.google.firebase.database.FirebaseDatabase


@Composable
fun LoginForm() {
    var userProfile by remember { mutableStateOf(UserProfileData()) }
    val context = LocalContext.current
    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            LoginField(
                value = userProfile.mejl,
                onChange = { data -> userProfile = userProfile.copy(mejl = data) },
                modifier = Modifier.fillMaxWidth()
            )
            PasswordField(
                value = userProfile.pwd,
                onChange = { data -> userProfile = userProfile.copy(pwd = data) },
                submit = {
                    if (!checkCredentials(userProfile, context)) userProfile = UserProfileData()
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            LabeledCheckbox(
                label = "Remember Me",
                onCheckChanged = {
                    userProfile = userProfile.copy(remember = !userProfile.remember)
                },
                isChecked = userProfile.remember
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (!checkCredentials(userProfile, context)) userProfile = UserProfileData()
                },
                enabled = userProfile.loginIsNotEmpty(),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(30.dp))
            // Natpis sa linkom
            val textLink = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Gray)) {
                    append("Немаш налог? ")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                    pushStringAnnotation(tag = "REGISTER", annotation = "register")
                    append("Региструј се")
                    pop()
                }
            }
            ClickableText(
                text = textLink,
                onClick = { offset ->
                    textLink.getStringAnnotations(
                        tag = "REGISTER",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        // Kreiranje Intent-a za pokretanje RegisterActivity
                        val intent = Intent(context, RegisterActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            )
        }
    }
}



fun checkCredentials(creds: UserProfileData, context: Context): Boolean {
    if (creds.loginIsNotEmpty()) {
        loginUserInFirebase(email=creds.mejl, pwd=creds.pwd, context=context)
        return true
    } else {
        Toast.makeText(context, "Wrong userProfile", Toast.LENGTH_SHORT).show()
        return false
    }
}

@Composable
fun LabeledCheckbox(
    label: String,
    onCheckChanged: () -> Unit,
    isChecked: Boolean
) {

    Row(
        Modifier
            .clickable(
                onClick = onCheckChanged
            )
            .padding(4.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = null)
        Spacer(Modifier.size(6.dp))
        Text(label)
    }
}

@Composable
fun LoginField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label:String = "Адреса е-поште",
    placeholder:String= "Унесите адресу ваше е-поште",
) {

    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Email,
            contentDescription = "Email Icon",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None
    )
}

@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Лозинка",
    placeholder: String = "Унеси лозинку"
) {

    var isPasswordVisible by remember { mutableStateOf(false) }

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Key,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }


    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}
@Preview(showBackground = true)
@Composable
fun LoginFormPreviews() {
    HandyManTheme {
        LoginForm()
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreviewsDark() {
    HandyManTheme(darkTheme = true) {
        LoginForm()
    }
}

fun loginUserInFirebase(email: String, pwd: String, context: Context) {
    FirebaseAuth.getInstance()
        .signInWithEmailAndPassword(email, pwd)
        .addOnSuccessListener {authResult -> Log.d("MyTag", "User signed in successfully with email: $email")

            // Dohvati uid korisnika
            val uid = authResult.user?.uid
            Toast.makeText(context, "User signed in successfully", Toast.LENGTH_SHORT).show()

                // Pristupi korisničkim podacima u Realtime Database
                val database = FirebaseDatabase.getInstance("https://handyman-a2aa1-default-rtdb.europe-west1.firebasedatabase.app").reference.child("users").child(uid!!)

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

                        // Preusmeri na MainActivity
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)

                        // Ako je potrebno da se zatvori trenutna aktivnost
                        if (context is Activity) {
                            (context as Activity).finish()
                        }
                    } else {
                        Log.e("MyTag", "No user data found for uid: $uid")
                        Toast.makeText(context, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Log.e("MyTag", "Failed to retrieve user data for uid: $uid", it)
                    Toast.makeText(context, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                }

        }
        .addOnFailureListener {
            Log.e("MyTag", "Failed to sign in user with email: $email")
            Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
        .addOnCanceledListener {
            Log.d("MyTag", "Sign in operation canceled for email: $email")
        }
}

fun logoutUser(context: Context) {
    FirebaseAuth.getInstance().signOut()
    // Opcionalno: Navigacija ili prikaz poruke nakon odjave
    Toast.makeText(context, "Uspešno ste se odjavili", Toast.LENGTH_SHORT).show()
    // Ovde možeš dodati kod za navigaciju na ekran za login
    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)


}