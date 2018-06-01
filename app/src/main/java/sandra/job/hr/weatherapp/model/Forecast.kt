package sandra.job.hr.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Forecast(val list: List<ForecastItem>)

data class ForecastItem(
        val main: WeatherValues,
        val weather: List<Weather>,
        @SerializedName("dt_txt") val timestamp: String
)

