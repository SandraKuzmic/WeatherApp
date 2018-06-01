package sandra.job.hr.weatherapp.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import retrofit2.Response
import sandra.job.hr.weatherapp.R
import sandra.job.hr.weatherapp.model.City
import sandra.job.hr.weatherapp.net.WeatherApi


class SearchActivity : AppCompatActivity() {

    private val weatherApi by lazy { WeatherApi.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (Intent.ACTION_SEARCH == intent.action) {
            preformSearch(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.item_search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false) //TODO set icon on left

        return true
    }

    private fun preformSearch(query: String) {
        disposable.add(weatherApi.getCurrentWeather(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<City>>() {
                    override fun onStart() {
                        startLoading()
                    }

                    override fun onComplete() {
                    }

                    override fun onNext(response: Response<City>) {
                        var hasError = true
                        if (response.code() == 200) {
                            hasError = false
                            response.body()?.apply {
                                tvCity.text = getString(R.string.city_country, name, sys.country)
                                tvCurrentTemp.text = getString(R.string.deg, main.temp)
                                tvWeather.text = weather.first().description.capitalize()
                                tvHumidity.text = getString(R.string.percentage, main.humidity)
                                tvTemperatureHigh.text = getString(R.string.deg, main.tempMax)
                                tvTemperatureLow.text = getString(R.string.deg, main.tempMin)
                                tvPressure.text = getString(R.string.hpa, main.pressure)
                                Picasso.get().load("http://api.openweathermap.org/img/w/${weather.first().icon}.png").into(ivWeatherIcon)
                            }
                        }
                        finishLoading(hasError)
                    }

                    override fun onError(e: Throwable) {
                        finishLoading(true)
                    }
                })
        )
    }

    private fun startLoading() {
        pbLoadCity.visibility = View.VISIBLE
        tvErrorCity.visibility = View.GONE
        contentCity.visibility = View.GONE
    }

    private fun finishLoading(hasError: Boolean) {
        pbLoadCity.visibility = View.GONE
        if (hasError){
            tvErrorCity.visibility = View.VISIBLE
            tvErrorCity.text = getString(R.string.city_not_found)
        } else {
            contentCity.visibility = View.VISIBLE
        }
    }
}
