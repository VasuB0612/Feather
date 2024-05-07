import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocation(navController: NavHostController) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.LightGray,
                        Color.DarkGray
                    )
                )
            )
    )
    {
        if (!locationPermissionState.status.isGranted) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_to_feather),
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Button(
                    onClick = {
                        locationPermissionState.launchPermissionRequest()
                    },
                    modifier = Modifier.padding(bottom = 30.dp),
                    colors = ButtonDefaults.buttonColors(Color(30, 30, 30))
                ) {
                    Text(
                        text = stringResource(id = R.string.permission_button),
                        color = Color.Magenta
                    )
                }
                Text(
                    text = stringResource(id = R.string.grant_access_text),
                    modifier = Modifier.padding(7.dp),
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            val location = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
            if(location != null){
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("latspread", "Latitude ${latitude} and Longitude ${longitude}")
                navController.navigate("WeatherScreenLatLong/${latitude}/${longitude}")
            }
        }
    }
}