package sandra.app.hr.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Weather(
        val main: String,
        val description: String,
        val icon: String
)

data class WeatherValues(
        val temp: Float,
        val pressure: Float,
        val humidity: Float,
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

data class Sys(
        val country: String,
        val sunrise: Long,
        val sunset: Long
)