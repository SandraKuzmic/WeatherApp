package sandra.app.hr.weatherapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_forecast.*
import net.danlew.android.joda.JodaTimeAndroid
import retrofit2.Response
import sandra.app.hr.weatherapp.R
import sandra.app.hr.weatherapp.adapters.RecyclerViewForecastAdapter
import sandra.app.hr.weatherapp.model.Forecast
import sandra.app.hr.weatherapp.model.ForecastItem
import sandra.app.hr.weatherapp.net.WeatherApi
import sandra.app.hr.weatherapp.utils.RESPONSE_OK
import sandra.app.hr.weatherapp.utils.checkConnection

class ForecastActivity : AppCompatActivity() {

    private val weatherApi by lazy { WeatherApi.create() }
    private val disposable = CompositeDisposable()

    private var forecast = mutableListOf<ForecastItem>()
    private lateinit var adapter: RecyclerViewForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        checkConnection(this)

        JodaTimeAndroid.init(this)

        val cityExtraKey = getString(R.string.city_extra)
        if (!intent.hasExtra(cityExtraKey)) {
            Toast.makeText(this, getString(R.string.no_city), Toast.LENGTH_SHORT).show()
            finish()
        }

        adapter = RecyclerViewForecastAdapter(this, forecast)
        rvForecast.layoutManager = LinearLayoutManager(this)
        rvForecast.adapter = adapter

        disposable.addAll(weatherApi.getForecast(intent.getStringExtra(cityExtraKey))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<Forecast>>() {
                    override fun onStart() {
                        startLoading()
                    }

                    override fun onComplete() {
                    }

                    override fun onNext(response: Response<Forecast>) {
                        if (response.code() == RESPONSE_OK) {
                            response.body()?.list?.let { forecast.addAll(it) }
                            adapter.notifyDataSetChanged()
                        }
                        finishLoading()
                    }

                    override fun onError(e: Throwable) {
                        Log.d("OkHttp", e.localizedMessage)
                        finishLoading()
                    }

                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    private fun startLoading() {
        pbLoadForecast.visibility = View.VISIBLE
        rvForecast.visibility = View.GONE
    }

    private fun finishLoading() {
        pbLoadForecast.visibility = View.GONE
        rvForecast.visibility = View.VISIBLE
    }
}
