package com.example.weatherapp

import android.Manifest
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
            App()
        }
    }
}

@Composable
fun weatherScreen(navController: NavHostController, latitude: Double, longitude: Double) {
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
            onCityReceived = { cityName ->
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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.offset(x = 37.dp, y = 16.dp),
        ) {
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                cityState.value?.let { city ->
                    Text(
                        text = city,
                        fontSize = 30.sp,
                        color = Color(20, 20, 20),
                        modifier = Modifier.padding(top = 150.dp)
                    )
                }
                Row {
                    descriptionState.value?.let { description ->
                        Log.d("Description", description)
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
                        Log.d("Temperature", temperature)
                        Text(
                            "${temperature} °C",
                            modifier = Modifier.padding(top = 150.dp),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color(20, 20, 20)
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxHeight().padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ){
                    Button(
                        onClick = {
                            navController.navigate("weatherScreenLatLong/${latitude}/${longitude}")
                        },
                        colors = ButtonDefaults.buttonColors(Color(20, 20, 20)),
                    ) {
                        Text(
                            "Current Location",
                            color = Color.Magenta
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
}
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestLocation(navController: NavHostController) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
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
                    text = "Welcome to Feather",
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
                        text = "Permission",
                        color = Color.Magenta
                    )
                }
                Text(
                    text = "Grant access to your location for accurate weather forecasts.",
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
                navController.navigate("weatherScreenLatLong/${latitude}/${longitude}")
            }
        }
    }
}

@Composable
fun App(){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "requestLocation") {
        composable("requestLocation") {
            requestLocation(navController)
        }
        composable("weatherScreen/{latitude}/{longitude}") {
            val lat = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val lon = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            weatherScreen(navController, lat, lon)
        }
        composable("weatherScreenLatLong/{latitude}/{longitude}") {
            val lat = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val lon = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            weatherScreenLatLong(navController, lat, lon)
        }
    }
}

@Composable
fun weatherScreenLatLong(navController: NavHostController, latitude: Double, longitude: Double){
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)
    var temperature by remember { mutableStateOf<Double?>(null) }
    var description by remember { mutableStateOf<String?>(null) }
    var cityName by remember { mutableStateOf<String?>(null) }

    val response = retrofit.getWeatherDataByLatLon(latitude, longitude, "d830da4c62dddd583303d8192c2e544c")
    response.enqueue(object : Callback<WeatherApp> {
        override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                temperature = responseBody.main.temp
                description = responseBody.weather.firstOrNull()?.description ?: "Unknown"
                cityName = responseBody.name
            }
        }
        override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            Log.e("WeatherAPI", "Failed to fetch weather data: ${t.message}")
        }
    })
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
        modifier = Modifier.fillMaxSize()
    ) {
        // Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Current Weather Forecast",
                fontSize = 32.sp
            )
        }
        // Content
        Box(
            modifier = Modifier.weight(1f).offset(y = (-90).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                cityName?.let { Text("$it", fontSize = 30.sp) }
                Spacer(modifier = Modifier.height(90.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    description?.let { Text("$it", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.width(100.dp))
                    temperature?.let {
                        val formattedTemperature = String.format("%.2f", it - 273.15)
                        Text("$formattedTemperature °C", fontSize = 40.sp )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    navController.navigate("weatherScreen/${latitude}/${longitude}")
                },
                colors = ButtonDefaults.buttonColors(Color(20, 20, 20))
            ) {
                Text(
                    "Other Cities",
                    color = Color.Magenta
                )
            }
        }
    }
}
fun fetchWeatherData1(
    cityName: String, onCityReceived: (String) -> Unit,
    onTemperatureReceived: (String) -> Unit,
    onHumidityReceived: (Double) -> Unit, onWindSpeedReceived: (Double) -> Unit,
    onSunRiseReceived: (String) -> Unit,
    onSunSetReceived: (String) -> Unit,
    onDescriptionReceived: (String) -> Unit
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    val response =
        retrofit.getWeatherData(cityName, "d830da4c62dddd583303d8192c2e544c", "metric")
    response.enqueue((object : Callback<WeatherApp> {
        override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body();
            if (response.isSuccessful && responseBody != null) {
                Log.d("response", responseBody.toString())
                val cityName = responseBody.name;
                val temperature = responseBody.main.temp.toString()
                val humidity = responseBody.main.humidity.toDouble()
                val windSpeed = responseBody.wind.speed.toDouble()
                val sunrise = responseBody.sys.sunrise.toString()
                val sunset = responseBody.sys.sunset.toString()
                val description = responseBody.weather.firstOrNull()?.description ?: "unknown"
                onCityReceived(cityName);
                onTemperatureReceived(temperature);
                onHumidityReceived(humidity);
                onWindSpeedReceived(windSpeed);
                onSunRiseReceived(sunrise);
                onSunSetReceived(sunset);
                onDescriptionReceived(description);
            }
        }

        override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            Log.d("Error", "Failed to fetch weather data: ${t.message}")
        }
    }))
}