import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun MapScreen(
    mapView: MapView,
    latLngText: String,
    onCurrentLocationClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 5.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            factory = { mapView }
        )

        Text(
            text = latLngText,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {

                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Back")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onCurrentLocationClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Current Location")
            }
        }
    }
}