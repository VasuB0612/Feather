package com.example.weatherapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.gson.internal.GsonBuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

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
    val cityState = remember { mutableStateOf(TextFieldValue()) }
    val fetchWeatherDataForCity = {
        temperatureState.value = null
        fetchWeatherData(cityState.value.text) { temperature ->
            temperatureState.value = temperature
        }
    }
    Column {
        // Text field for entering the city name
        TextField(
            value = cityState.value,
            onValueChange = { cityState.value = it },
            label = { Text("Enter City Name") }
        )
        // Button to trigger weather data fetching for the entered city
        Button(onClick = { fetchWeatherDataForCity() }) {
            Text("Search")
        }
        // Display the temperature on the screen
        Surface(modifier = Modifier.fillMaxSize()) {
            temperatureState.value?.let { temperature ->
                Text(text = "Temperature: $temperature")
            }
        }
    }
}

fun fetchWeatherData(cityName: String, onTemperatureReceived: (String) -> Unit){
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter factory
        .build()
        .create(ApiInterface::class.java)

    val response = retrofit.getWeatherData(cityName, "d830da4c62dddd583303d8192c2e544c", "metric")
    response.enqueue((object : Callback<WeatherApp>{
        override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body();
            if (response.isSuccessful && responseBody != null){
                val temperature = responseBody.main.temp.toString()
                onTemperatureReceived(temperature)
                Log.d("TAG", "$temperature")
            }
        }

        override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            TODO("Not yet implemented")
        }

    }))
}