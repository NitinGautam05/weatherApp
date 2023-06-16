package com.example.quickweather

import android.widget.TextView
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val BASE_URL= "https://api.openweathermap.org/"
const val API_KEY = "579554eb3f061426bcc6cbf8287010ac"

interface WeatherAPI {
    @GET("data/2.5/weather?appid=$API_KEY")
    fun getWeatherData(@Query("q") cityName: String): Call<WeatherData>
}

object Retrofitinstance {

    val weatherApiService : WeatherAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherApiService = retrofit.create(WeatherAPI::class.java)

    }

}
