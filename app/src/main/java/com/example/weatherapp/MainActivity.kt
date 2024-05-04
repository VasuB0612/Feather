package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val (permissionGranted, setPermissionGranted) = remember { mutableStateOf(false) }

            requestLocation {
                setPermissionGranted(true)
            }

            if (permissionGranted) {
                weatherScreen()
            }
        }
    }
}

@Composable
fun weatherScreen(){
    val temperatureState = remember { mutableStateOf<String?>(null) }
    val humidityState = remember { mutableStateOf<Double?>(null) }
    val windSpeedState = remember { mutableStateOf<Double?>(null) }
    val sunriseState = remember { mutableStateOf<String?>(null) }
    val sunsetState = remember { mutableStateOf<String?>(null) }
    val descriptionState = remember { mutableStateOf<String?>(null) }
    val cityState = remember { mutableStateOf<String?>(null) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val fetchWeatherDataForCity = {
        temperatureState.value = null
        humidityState.value = null
        windSpeedState.value = null
        sunriseState.value = null
        sunsetState.value = null
        descriptionState.value = null
        cityState.value = null

        fetchWeatherData1(
            cityName = nameState.value.text,
            onCityReceived ={ cityName ->
                cityState.value = cityName
            },
            onTemperatureReceived = { temperature ->
                temperatureState.value = temperature
            },
            onHumidityReceived = { humidity ->
                humidityState.value = humidity
            },
            onWindSpeedReceived = { windSpeed ->
                windSpeedState.value = windSpeed
            },
            onSunRiseReceived = { sunrise ->
                sunriseState.value = sunrise
            },
            onSunSetReceived = { sunset ->
                sunsetState.value = sunset
            },
            onDescriptionReceived = { desc ->
                descriptionState.value = desc
            }
        )
    }

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
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    )
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ){
        Row(
            modifier = Modifier.offset(x = 37.dp, y = 16.dp),
        ){
            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Enter City Name") },
                modifier = Modifier
                    .width(220.dp)
                    .height(48.dp)
                    .offset(x = 0.dp, y = 8.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Button(
                modifier = Modifier.offset(x = 0.dp, y = 7.dp),
                onClick = { fetchWeatherDataForCity() },
                colors = ButtonDefaults.buttonColors(Color(30, 30, 30))
            ) {
                Text(
                    "Search",
                    color = Color.White,
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            cityState.value?.let{ city ->
                Text(
                    text = city,
                    fontSize = 30.sp,
//                    color = Color(255, 228, 196),
                    color = Color(20, 20, 20),
                    modifier = Modifier.padding(top = 150.dp)
                )
            }
            Row {
                descriptionState.value?.let{ description ->
                    Text(
                        description,
                        modifier = Modifier
                            .padding(top = 162.dp)
                            .padding(end = 60.dp),
                        color = Color(20, 20, 20),
                        fontSize = 19.sp
                    )
                }
                temperatureState.value?.let { temperature ->
                    Text(
                        text = "$temperature °C",
                        modifier = Modifier.padding(top = 150.dp),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(20, 20, 20)
                    )
                }
            }
//            humidityState.value?.let { humidity ->
//                Text(text = "Humidity: $humidity")
//            }
//            windSpeedState.value?.let { windspeed ->
//                Text(text = "Wind Speed: $windspeed")
//            }
//            sunriseState.value?.let { sunrise ->
//                Text(text = "Sunrise: $sunrise")
//            }
//            sunsetState.value?.let { sunset ->
//                Text(text = "Sunset: $sunset")
//            }
        }
    }
}
@SuppressLint("MissingPermission", "PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestLocation(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_COARSE_LOCATION
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
            Text("Permission")
        }

        if (locationPermissionState.status.isGranted) {
            val location = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
            if (location != null) {
                fetchWeatherData2(location.latitude, location.longitude)
            }
            onPermissionGranted()
        } else {
            Column (){
                Text(
                    "Grant location access to show weather forecast.",
                    fontSize = 15.sp
                )
            }
        }
    }
}

fun fetchWeatherData2(latitude: Double, Longitude: Double){

}

fun fetchWeatherData1(cityName: String, onCityReceived: (String) -> Unit,
                     onTemperatureReceived: (String) -> Unit,
                     onHumidityReceived: (Double) -> Unit, onWindSpeedReceived: (Double) -> Unit,
                     onSunRiseReceived: (String) -> Unit,
                     onSunSetReceived: (String) -> Unit,
                     onDescriptionReceived: (String) -> Unit){
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    val response = retrofit.getWeatherData(cityName, "d830da4c62dddd583303d8192c2e544c", "metric")
    response.enqueue((object : Callback<WeatherApp>{
        override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body();
            if (response.isSuccessful && responseBody != null){
                val cityName = responseBody.name;
                val temperature = responseBody.main.temp.toString()
                val humidity = responseBody.main.humidity.toDouble()
                val windSpeed = responseBody.wind.speed.toDouble()
                val sunrise = responseBody.sys.sunrise.toString()
                val sunset = responseBody.sys.sunset.toString()
                val description = responseBody.weather.firstOrNull()?.main?: "unknown"
                onCityReceived(cityName);
                onTemperatureReceived(temperature);
                onHumidityReceived(humidity);
                onWindSpeedReceived(windSpeed);
                onSunRiseReceived(sunrise);
                onSunSetReceived(sunset);
                onDescriptionReceived(description);
                Log.d("TAG", responseBody.toString())
            }
        }

        override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            TODO("Not yet implemented")
        }

    }))
}