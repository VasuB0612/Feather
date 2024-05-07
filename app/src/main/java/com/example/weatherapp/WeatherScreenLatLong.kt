import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.ApiInterface
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun WeatherScreenLatLong(navController: NavHostController, latitude: Double, longitude: Double){
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)
    var temperature by remember { mutableStateOf<Double?>(null) }
    var description by remember { mutableStateOf<String?>(null) }
    var placeName by remember { mutableStateOf<String?>(null) }

    val response = retrofit.getWeatherDataByLatLon(latitude, longitude, "d830da4c62dddd583303d8192c2e544c")
    response.enqueue(object : Callback<WeatherApp> {
        override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                temperature = responseBody.main.temp
                description = responseBody.weather.firstOrNull()?.description ?: "Unknown"
                placeName = responseBody.name
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
                text = stringResource(id = R.string.title),
                fontSize = 32.sp
            )
        }
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .offset(y = (-90).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                placeName?.let { Text("$it", fontSize = 30.sp) }
                Spacer(modifier = Modifier.height(80.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column{
                        val drawableResId = when (description?.lowercase()) {
                            "sunny", "clear sky", "clear" -> R.drawable.sunny
                            "light rain", "drizzle", "moderate rain", "showers", "heavy rain" -> R.drawable.rainy
                            "scattered clouds", "few clouds", "overcast clouds", "broken clouds", "partly clouds", "overcast", "clouds", "mist", "haze", "foggy" -> R.drawable.cloudy
                            "light snow", "heavy snow", "moderate snow", "blizzard" -> R.drawable.snowy
                            else -> null
                        }
                        drawableResId?.let { resourceId ->
                            Image(
                                painter = painterResource(id = resourceId),
                                contentDescription = "Weather Icon: $description",
                                modifier = Modifier.size(135.dp)
                            )
                        }
                        Box(
                            Modifier.align(Alignment.CenterHorizontally)
                        ){
                            description?.let { description ->
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
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(70.dp))
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
                    stringResource(id = R.string.other_cities_button),
                    color = Color.Magenta
                )
            }
        }
    }
}