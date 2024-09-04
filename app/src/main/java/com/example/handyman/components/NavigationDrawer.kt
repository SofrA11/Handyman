package com.example.handyman.components
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.handyman.view.logoutUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter
import com.example.handyman.activity.LokacijaActivity
import com.example.handyman.data.UserSession
import com.example.handyman.view.HomePage
import com.example.handyman.view.ProfileScreen

enum class MainRoute(value: String) {
    Profile("articles"),
    About("about"),
    Settings("settings"),
    Logout("logout"),
    Home("home"),
    Map("map"),
}

private data class DrawerMenu(val icon: ImageVector, val title: String, val route: String)

private val menus = arrayOf(
    DrawerMenu(Icons.Filled.Face, "Профил", MainRoute.Profile.name),
    DrawerMenu(Icons.Filled.Settings, "Подешавања", MainRoute.Settings.name),
    DrawerMenu(Icons.Filled.Info, "О нама", MainRoute.About.name),
    DrawerMenu(Icons.Filled.Logout, "Одјави се", MainRoute.Logout.name)
)

@Composable
private fun DrawerContent(
    menus: Array<DrawerMenu>,
    onMenuClick: (String) -> Unit
) {
    val userImageUrl = UserSession.imageUrl
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Prikaz slike u okviru kruga
            if(userImageUrl==null){
                Image(
                    modifier = Modifier.size(150.dp),
                    imageVector = Icons.Filled.AccountCircle,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            else{
                Image(
                    painter = rememberAsyncImagePainter(model = userImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape) // Oblikuje sliku u krug
                )
                Spacer(modifier = Modifier.height(8.dp)) // Razmak između slike i teksta
                Text(
                    text = UserSession.ime.toString()+" "+UserSession.prezime.toString(),
                    style = MaterialTheme.typography.bodyLarge // Stil teksta
                )
            }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        menus.forEach {
            NavigationDrawerItem(
                label = { Text(text = it.title) },
                icon = { Icon(imageVector = it.icon, contentDescription = null) },
                selected = false,
                onClick = {
                    onMenuClick(it.route)
                }
            )
        }
    }
}

@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    val context =  LocalContext.current
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                }
                DrawerContent(menus) { route ->
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(route)
                }
            }
        },
        gesturesEnabled = false

    ) {
        Box(Modifier.fillMaxSize()) {

        NavHost(navController = navController, startDestination = MainRoute.Home.name) {
            composable(MainRoute.Profile.name) {
                ProfileScreen()
            }
            composable(MainRoute.About.name) {
                //AboutScreen(drawerState)
            }
            composable(MainRoute.Settings.name) {
                // SettingsScreen(drawerState)
            }
            composable(MainRoute.Logout.name) {
                logoutUser(context)
            }
            composable(MainRoute.Home.name) {
              HomePage()
            }
            composable(MainRoute.Map.name){
                val intent = Intent(context, LokacijaActivity::class.java)
                context.startActivity(intent)
            }
        }

        // Optional: Provide a visible element to open drawer
        // Adjust this to your needs, e.g., button or gesture recognizer
        IconButton(onClick = {
            coroutineScope.launch {
                drawerState.open()
            }
        }) {
            Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
        }
    }
    }
}
