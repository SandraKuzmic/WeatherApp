package sandra.app.hr.weatherapp.model

data class City(
        val name: String,
        val weather: List<Weather>,
        val main: WeatherValues,
        val visibility: Int,
        val wind: Wind,
        val clouds: Cloud,
        val sys: Sys
)