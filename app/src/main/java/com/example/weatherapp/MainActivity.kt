package com.example.weatherapp

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            weatherScreen()
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
    val cityState = remember { mutableStateOf(TextFieldValue()) }
    val fetchWeatherDataForCity = {
        temperatureState.value = null
        humidityState.value = null
        windSpeedState.value = null
        sunriseState.value = null
        sunsetState.value = null
        descriptionState.value = null

        fetchWeatherData(
            cityName = cityState.value.text,
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

//    Box(
//        modifier = Modifier.fillMaxSize().background(
//            brush = Brush.verticalGradient(
//                colors = listOf(
//                    Color(0xFF6A4A92), // Start color (purple)
//                    Color(0xFF42275A)  // End color (darker purple)
//                )
//            )
//        )
//    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ){
        Row(
            modifier = Modifier.offset(x = 37.dp, y = 16.dp),
        ){
            TextField(
                value = cityState.value,
                onValueChange = { cityState.value = it },
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
                    color = Color(255, 228, 196),
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (cityState.value.text.isNotBlank()) {
                Text(
                    text = cityState.value.text,
                    fontSize = 30.sp,
                    color = Color(255, 228, 196),
                    modifier = Modifier.padding(top = 150.dp)
                )
            }
            Row {
                descriptionState.value?.let{ description ->
                    Text(
                        description,
                        modifier = Modifier.padding(top = 157.dp).padding(end = 60.dp),
                        color = Color(255, 228, 196),
                        fontSize = 19.sp
                    )
                }
                temperatureState.value?.let { temperature ->
                    Text(
                        text = "$temperature °C",
                        modifier = Modifier.padding(top = 150.dp),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(255, 228, 196),
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

fun fetchWeatherData(cityName: String, onTemperatureReceived: (String) -> Unit,
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
                val temperature = responseBody.main.temp.toString()
                val humidity = responseBody.main.humidity.toDouble()
                val windSpeed = responseBody.wind.speed.toDouble()
                val sunrise = responseBody.sys.sunrise.toString()
                val sunset = responseBody.sys.sunset.toString()
                val description = responseBody.weather.firstOrNull()?.main?: "unknown"
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