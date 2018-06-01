package sandra.job.hr.weatherapp.model

import com.google.gson.annotations.SerializedName

data class City(
        val name: String,
        val weather: List<Weather>,
        val main: WeatherValues,
        val visibility: Int,
        val wind: Wind,
        val clouds: Cloud,
        val sys: Sys
)

data class Weather(
        val main: String,
        val description: String,
        val icon: String
)

data class WeatherValues(
        val temp: Float,
        val pressure: Int,
        val humidity: Int,
        @SerializedName("temp_min") val tempMin: Float,
        @SerializedName("temp_max") val tempMax: Float
)

data class Wind(
        val speed: Float,
        val deg: Int
)

data class Cloud(
        val all: Int
)

data class Sys (
    val country: String,
    val sunrise: Long,
    val sunset: Long
)