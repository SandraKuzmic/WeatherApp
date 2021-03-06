package sandra.app.hr.weatherapp.net

import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import sandra.app.hr.weatherapp.model.City
import sandra.app.hr.weatherapp.model.Forecast

private const val API_KEY = "27cffafb9603dab3ffd83c8dfc110e89"

private const val weatherBaseUrl = "http://api.openweathermap.org/"

fun imageUrl(icon: String) = "http://api.openweathermap.org/img/w/$icon.png"

interface WeatherApi {

    @GET("data/2.5/weather")
    fun getCurrentWeather(
            @Query("q") city: String,
            @Query("APPID") appId: String = API_KEY,
            @Query("units") units: String = "metric"
    ): Observable<Response<City>>

    @GET("data/2.5/forecast")
    fun getForecast(
            @Query("q") city: String,
            @Query("APPID") appId: String = API_KEY,
            @Query("units") units: String = "metric"
    ): Observable<Response<Forecast>>

    companion object {
        fun create(): WeatherApi {
            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

            //logging OkHttp
            val okHttpClientBuilder = OkHttpClient.Builder()

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            okHttpClientBuilder.addInterceptor(logging)

            val retrofit = Retrofit.Builder()
                    .baseUrl(weatherBaseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClientBuilder.build())
                    .build()

            return retrofit.create(WeatherApi::class.java)
        }
    }
}