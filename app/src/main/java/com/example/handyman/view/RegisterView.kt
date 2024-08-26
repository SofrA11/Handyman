package com.example.handyman.view

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handyman.MainActivity
import com.example.handyman.activity.LoginActivity
import com.example.handyman.ui.theme.HandyManTheme
import com.example.handyman.data.UserProfileData
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.google.firebase.BuildConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import androidx.core.app.ActivityCompat
import com.example.handyman.data.RealtimeDatabase
import com.example.handyman.data.UserData
import com.google.firebase.auth.FirebaseAuth
import network.chaintech.cmpimagepickncrop.CMPImageCropDialog
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper
import com.example.handyman.view.loginUserInFirebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Composable
fun RegisterForm() {

    var userProfile by remember { mutableStateOf(UserProfileData()) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    checkAndRequestReadExternalStoragePermissions(context)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        userProfile = userProfile.copy(fotoUri = uri ?: Uri.EMPTY)
    }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            userProfile = userProfile.copy(fotoUri = imageUri ?: Uri.EMPTY)
        }
    }

    val fileProvider = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            createImageFile(context)
        )
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {


        Box(
            modifier = Modifier
                .size(150.dp)
                .border(4.dp, Color.Gray) // You can customize the border color and width
        ) {
            // Displaying image if available
            userProfile.fotoUri?.let { uri ->
                Image(painter = rememberImagePainter(uri), contentDescription = null, modifier = Modifier.size(150.dp))
            }
            } ?: run {
                // Optionally, you can put a placeholder or leave it empty

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(5.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = "Galerija"
                )
                Spacer(modifier = Modifier.width(8.dp)) // Razmak između ikone i teksta
                Text("Галерија")
            }
            val selectedBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
            val imageCropper = rememberImageCropper()
            CMPImageCropDialog(imageCropper = imageCropper)
            // Dugme za otvaranje kamere i slikanje slika
            Button(
                onClick = {
                    checkAndRequestCameraPermissions(context)
                    imageUri = fileProvider
                    takePictureLauncher.launch(fileProvider)
                },
                shape = RoundedCornerShape(5.dp),

            ) {
                Icon(
                    imageVector = Icons.Filled.Camera,
                    contentDescription = "Kamera"
                )
                Spacer(modifier = Modifier.width(8.dp)) // Razmak između ikone i teksta
                Text("Камера")
            }
        }

       // Spacer(modifier = Modifier.height(20.dp))

        EmailField(
            value = userProfile.mejl,
            onChange = { data -> userProfile = userProfile.copy(mejl = data) },
            modifier = Modifier.fillMaxWidth()
        )
        NameField(
            value = userProfile.ime,
            onChange = { data -> userProfile = userProfile.copy(ime = data) },
            modifier = Modifier.fillMaxWidth()
        )
        SurnameField(
            value = userProfile.prezime,
            onChange = { data -> userProfile = userProfile.copy(prezime = data) },
            modifier = Modifier.fillMaxWidth()
        )
        UsernameField(
            value = userProfile.korisnickoIme,
            onChange = { data -> userProfile = userProfile.copy(korisnickoIme = data) },
            modifier = Modifier.fillMaxWidth()
        )
        PhoneField(
            value = userProfile.brojTelefona,
            onChange = { data -> userProfile = userProfile.copy(brojTelefona = data) },
            modifier = Modifier.fillMaxWidth()
        )
        PasswordFieldRegist(
            pwdValue = userProfile.pwd,
            email = userProfile.mejl,
            onChange = { data -> userProfile = userProfile.copy(pwd = data) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledCheckbox(
            label = "Остани пријављен",
            onCheckChanged = {
                userProfile = userProfile.copy(remember = !userProfile.remember)
            },
            isChecked = userProfile.remember
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (!checkUserProfileData(userProfile, context)) userProfile = UserProfileData()
            },
            enabled = userProfile.isNotEmpty(),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Региструј ме!")
        }
        Spacer(modifier = Modifier.height(30.dp))
        // Natpis sa linkom
        val textLink = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Gray)) {
                append("Већ поседујеш налог? ")
            }
            withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                pushStringAnnotation(tag = "LOGIN", annotation = "login")
                append("Пријави се!")
                pop()
            }
        }
        ClickableText(
            text = textLink,
            onClick = { offset ->
                textLink.getStringAnnotations(
                    tag = "LOGIN",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    // Kreiranje Intent-a za pokretanje LoginActivity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }
            }
        )
    }
}

private val PERMISSION_CODE = 1000
private fun checkAndRequestCameraPermissions(context: Context) {
    val cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
    val storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    if (cameraPermission != PackageManager.PERMISSION_GRANTED || storagePermission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
    }
}


private fun checkAndRequestReadExternalStoragePermissions(context: Context) {
    val readStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

    if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
    }
}

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir = context.getExternalFilesDir(null)
    return File.createTempFile(
        "IMG_$timeStamp", /* prefiks */
        ".jpg", /* sufiks */
        storageDir /* direktorijum */
    )
}


 fun checkUserProfileData(user: UserProfileData, context: Context): Boolean {
    if (user.isNotEmpty()) {
        createUserInFirebase(user=user, context = context)
        return true
    } else {
        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        return false
    }
}

@Composable
fun LabeledCheckboxReg(label: String, onCheckChanged: () -> Unit, isChecked: Boolean) {
    Row(
        Modifier
            .clickable(onClick = onCheckChanged)
            .padding(4.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = null)
        Spacer(Modifier.size(6.dp))
        Text(label)
    }
}

@Composable
fun RegisterField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Реиструј се",
    placeholder: String = "Унесите адресу е-поште"
) {

    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Key,
            contentDescription = "",
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
fun PasswordFieldRegist(pwdValue: String, email: String, onChange: (String) -> Unit, modifier: Modifier = Modifier, label: String = "Лозинка", placeholder: String = "Унеси лозинку") {

    var isPasswordVisible by remember { mutableStateOf(false) }

    val leadingIcon = @Composable {
        Icon(Icons.Default.Key, contentDescription = "",tint = MaterialTheme.colorScheme.primary)
    }
    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) {
                    Icons.Default.VisibilityOff
                } else{
                    Icons.Default.Visibility
                },
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    val context = LocalContext.current

    TextField(
        value = pwdValue,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone={Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()}
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun BaseTextField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String,
    leadingIcon: (@Composable () -> Unit)? = null // Dodano opcionalno polje za ikonu
) {
    val focusManager = LocalFocusManager.current

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
fun NameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseTextField(
        value = value,
        onChange = onChange,
        modifier = modifier,
        label = "Име",
        placeholder = "Унесите име",
        leadingIcon = {
            Icon(
                Icons.Default.Person,
                contentDescription = "Ime Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
fun SurnameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseTextField(
        value = value,
        onChange = onChange,
        modifier = modifier,
        label = "Презиме",
        placeholder = "Унесите презиме",
        leadingIcon = {
            Icon(
                Icons.Default.Person,
                contentDescription = "Prezime Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
fun UsernameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseTextField(
        value = value,
        onChange = onChange,
        modifier = modifier,
        label = "Корисничко име",
        placeholder = "Унесите корисничко име",
        leadingIcon = {
            Icon(
                Icons.Default.PersonOutline,
                contentDescription = "Username Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
fun EmailField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseTextField(
        value = value,
        onChange = onChange,
        modifier = modifier,
        label = "Адреса е-поште",
        placeholder = "Унесите адресу ваше е-поште",
        leadingIcon = {
            Icon(
                Icons.Default.Email,
                contentDescription = "Email Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
fun PhoneField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseTextField(
        value = value,
        onChange = onChange,
        modifier = modifier,
        label = "Број телефона",
        placeholder = "Унесите број телефона",
        leadingIcon = {
            Icon(
                Icons.Default.Phone,
                contentDescription = "Phone Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}


 fun createUserInFirebase(user: UserProfileData, context: Context) {
     //Creating member variables of FirebaseAuth
      var mAuth: FirebaseAuth?=null
     mAuth=FirebaseAuth.getInstance()

     mAuth!!.createUserWithEmailAndPassword(user.mejl, user.pwd)
         .addOnCompleteListener{
             Toast.makeText(context, "Registracija completed", Toast.LENGTH_SHORT).show()
        }
        .addOnSuccessListener { task ->
            val firebaseUser = task.user
            val userId = firebaseUser?.uid
            user.uid=userId!!

            Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()

            savePicture(user=user,context=context)
         //   saveUser(user=user, context=context)
            loginUserInFirebase(user.mejl,user.pwd,context)
        }
        .addOnFailureListener {
            Log.e("MyTag", "Failed to create user with email: ${user.mejl}")
            Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
        }
        .addOnCanceledListener {
            Log.d("MyTag", "Operation canceled for email: $user.mejl")
        }
}

fun saveUser(user: UserProfileData, context: Context) {
    val firebaseDatabaseInstance = FirebaseDatabase.getInstance("https://handyman-a2aa1-default-rtdb.europe-west1.firebasedatabase.app")
    val firebaseDatabaseReference = firebaseDatabaseInstance.getReference("users")
   // val empId = firebaseDatabaseReference.push().key!!

    val newUser = UserData(
        mejl = user.mejl,
        ime = user.ime,
        prezime = user.prezime,
        korisnickoIme = user.korisnickoIme,
        brojTelefona = user.brojTelefona,
        uid = user.uid,
        imageUrl=user.pictureUrl
    )

    firebaseDatabaseReference.child(newUser.uid!!).setValue(newUser)
        .addOnCompleteListener{
            Toast.makeText(context, "Upis completed", Toast.LENGTH_SHORT).show()
        }
        .addOnSuccessListener {
            Toast.makeText(context, "Upis successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Upis failed ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}

fun savePicture(user:UserProfileData,context: Context){
    var storageRef:StorageReference
    storageRef = FirebaseStorage.getInstance().getReference("ProfileImages/${user.mejl}");

    storageRef.putFile(user.fotoUri)
        .addOnSuccessListener { taskSnapshot ->
            // Slika je uspešno uploadovana
            // Možeš dobiti URL do slike pomoću:
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                user.pictureUrl=downloadUrl
                saveUser(user=user, context=context)
            }
        }
        .addOnFailureListener { exception ->
            // Upravljanje greškama tokom uploadovanja
            exception.printStackTrace()
        }
}