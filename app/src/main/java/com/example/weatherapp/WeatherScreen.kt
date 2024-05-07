import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.ApiInterface
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApp
//import com.example.weatherapp.fetchWeatherData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun WeatherScreen(navController: NavHostController, latitude: Double, longitude: Double) {
    val temperatureState = remember { mutableStateOf<String?>(null) }
    val descriptionState = remember { mutableStateOf<String?>(null) }
    val cityState = remember { mutableStateOf<String?>(null) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val fetchWeatherDataForCity = {
        temperatureState.value = null
        descriptionState.value = null
        cityState.value = null

        fetchWeatherData(
            cityName = nameState.value.text,
            onCityReceived = { cityName ->
                cityState.value = cityName
            },
            onTemperatureReceived = { temperature ->
                temperatureState.value = temperature
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
                label = { Text(stringResource(id = R.string.label_text)) },
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
                    text = stringResource(id = R.string.search_button_text),
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
                    Column {
                        descriptionState.value?.let { description ->
                            val drawableResId = when (description.lowercase()) {
                                "sunny", "clear sky", "clear" -> R.drawable.sunny
                                "light rain", "drizzle", "moderate rain", "showers", "heavy rain" -> R.drawable.rainy
                                "scattered clouds", "few clouds","overcast clouds", "broken clouds", "partly clouds", "overcast", "clouds", "mist", "haze", "foggy" -> R.drawable.cloudy
                                "light snow", "heavy snow", "moderate snow", "blizzard" -> R.drawable.snowy
                                else -> null
                            }
                            drawableResId?.let { resourceId ->
                                Image(
                                    painter = painterResource(id = resourceId),
                                    contentDescription = "Weather Icon: $description",
                                    modifier = Modifier
                                        .size(130.dp)
                                        .offset(y = 103.dp)
                                )
                            }
                        }
                        Box(
                            Modifier
                                .offset(y = (-50).dp)
                                .align(Alignment.CenterHorizontally)
                        ){
                            descriptionState.value?.let { description ->
                                val resourceId = when (description.lowercase()) {
                                    "sunny" -> R.string.sunny
                                    "clear sky" -> R.string.clear_sky
                                    "clear" -> R.string.clear
                                    "light rain" -> R.string.light_rain
                                    "drizzle" -> R.string.drizzle
                                    "moderate rain" -> R.string.moderate_rain
                                    "showers" -> R.string.showers
                                    "heavy rain" -> R.string.heavy_rain
                                    "scattered clouds" -> R.string.scattered_clouds
                                    "few clouds" -> R.string.few_clouds
                                    "overcast clouds" -> R.string.overcast_clouds
                                    "broken clouds" -> R.string.broken_clouds
                                    "partly clouds" -> R.string.partly_clouds
                                    "overcast" -> R.string.overcast
                                    "clouds" -> R.string.clouds
                                    "mist" -> R.string.mist
                                    "haze" -> R.string.haze
                                    "foggy" -> R.string.foggy
                                    "light snow" -> R.string.light_snow
                                    "heavy snow" -> R.string.heavy_snow
                                    "moderate snow" -> R.string.moderate_snow
                                    "blizzard" -> R.string.blizzard

                                    else -> R.string.default_description
                                }
                                Text(
                                    text = stringResource(id = resourceId),
                                    modifier = Modifier.padding(top = 162.dp),
                                    color = Color(20, 20, 20),
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    temperatureState.value?.let { temperature ->
                        Text(
                            "${temperature} °C",
                            modifier = Modifier.padding(top = 150.dp),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color(20, 20, 20)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ){
                    Button(
                        onClick = {
                            navController.navigate("WeatherScreenLatLong/${latitude}/${longitude}")
                        },
                        colors = ButtonDefaults.buttonColors(Color(20, 20, 20)),
                    ) {
                        Text(
                            stringResource(id = R.string.current_location_button),
                            color = Color.Magenta
                        )
                    }
                }
            }
        }
    }
}

fun fetchWeatherData(
    cityName: String, onCityReceived: (String) -> Unit,
    onTemperatureReceived: (String) -> Unit,
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
                val description = responseBody.weather.firstOrNull()?.description ?: "unknown"
                onCityReceived(cityName);
                onTemperatureReceived(temperature);
                onDescriptionReceived(description);
            }
        }

        override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            Log.d("Error", "Failed to fetch weather data: ${t.message}")
        }
    }))
}