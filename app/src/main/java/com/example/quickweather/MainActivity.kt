package com.example.quickweather

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var localDateTime : LocalDateTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val address = findViewById<EditText>(R.id.address)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        // Retrieve the saved city from SharedPreferences
        val savedCity = sharedPreferences.getString("city", "")
        address.setText(savedCity)

        address.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val cityN = textView.text.toString()
                Log.d("Weather Details", "CityN: $cityN")
                getNews(cityN)
                // Save the city to SharedPreferences
                saveCity(cityN)
                true
            } else {
                false
            }
        }

        if (!savedCity.isNullOrEmpty()){
            getNews(savedCity)
        }

    }

    private fun saveCity(city: String) {
        val editor = sharedPreferences.edit()
        editor.putString("city", city)
        editor.apply()
    }

    fun getNews(cityName:String) {
        val weather = Retrofitinstance.weatherApiService.getWeatherData(cityName)
        weather.enqueue(object : Callback<WeatherData> {

            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                val result = response.body()
                if (result != null) {

                    Log.d("weather-Information", result.toString())

                    val time = findViewById<TextView>(R.id.updated_at)
                    val status = findViewById<TextView>(R.id.status)
                    val temp = findViewById<TextView>(R.id.temp)
                    val temp_min = findViewById<TextView>(R.id.temp_min)
                    val temp_max = findViewById<TextView>(R.id.temp_max)
                    val pressure = findViewById<TextView>(R.id.pressure)
                    val sunrise = findViewById<TextView>(R.id.sunrise)
                    val sunset = findViewById<TextView>(R.id.sunset)
                    val humidity = findViewById<TextView>(R.id.humidity)
                    val wind = findViewById<TextView>(R.id.wind)

                    localDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(result.dt),
                        ZoneId.systemDefault()
                    )

                    time.text = "Updated at " + localDateTime.hour.toString()+":"+localDateTime.minute.toString()
                    status.text = result.weather[0].main

                    var tempC = result.main.temp
                    tempC = (tempC - 273.15)
                    temp.text = (tempC).toInt().toString() + "°C"

                    var tempMin = result.main.temp_min
                    tempMin = (tempMin - 273.15)
                    var tempMax = result.main.temp_max
                    tempMax = (tempMax - 273.15) + 1

                    temp_min.text = "Min Temp: " + (tempMin).toInt().toString() + "°C"
                    temp_max.text = "Max Temp: " + (tempMax).toInt().toString() + "°C"

                    pressure.text = (result.main.pressure).toString() + " mbar"
                    humidity.text = (result.main.humidity).toString() + "%"
                    wind.text = result.wind.speed.toString() + "m/s"

                     localDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(result.sys.sunrise),
                        ZoneId.systemDefault()
                    )
                    var hours = localDateTime.hour
                    var minutes = localDateTime.minute
                    sunrise.text = (hours).toString() + ":" + (minutes).toString() + "AM"

                    localDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(result.sys.sunset),
                        ZoneId.systemDefault()
                    )
                    hours = localDateTime.hour
                    minutes = localDateTime.minute
                    sunset.text = (hours).toString() + ":" + (minutes).toString() + "PM"
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.d("weatherInformation", "Error hai in fetching weather", t)
            }

        })
    }
}
