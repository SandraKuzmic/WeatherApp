package sandra.app.hr.weatherapp.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import retrofit2.Response
import sandra.app.hr.weatherapp.R
import sandra.app.hr.weatherapp.model.City
import sandra.app.hr.weatherapp.net.WeatherApi
import sandra.app.hr.weatherapp.net.imageUrl
import sandra.app.hr.weatherapp.utils.RESPONSE_OK
import sandra.app.hr.weatherapp.utils.checkConnection


class SearchActivity : AppCompatActivity() {

    private val weatherApi by lazy { WeatherApi.create() }
    private val disposable = CompositeDisposable()
    private lateinit var city: String
    private lateinit var video: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        checkConnection(this)

        handleIntent(intent)

        tvShowForecast.setOnClickListener {
            val intent = Intent(this@SearchActivity, ForecastActivity::class.java)
            intent.putExtra(getString(R.string.city_extra), city)
            startActivity(intent)
        }

        tvShowVideo.setOnClickListener {
            val intent = Intent(this@SearchActivity, VideoActivity::class.java)
            intent.putExtra(getString(R.string.video_extra), video)
            startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
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
        searchView.setIconifiedByDefault(true)

        menu.findItem(R.id.item_search).setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                contentWelcome.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                if (pbLoadCity.visibility == View.VISIBLE) {
                    return false //to finish loading first
                }
                tvErrorCity.visibility = View.GONE
                contentCity.visibility = View.GONE
                contentWelcome.visibility = View.VISIBLE
                return true
            }
        })

        return true
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            checkConnection(this)
            preformSearch(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    private fun preformSearch(query: String) {
        city = query
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
                        if (response.code() == RESPONSE_OK) {
                            hasError = false
                            response.body()?.apply {
                                tvCity.text = getString(R.string.city_country, name, sys.country)
                                tvCurrentTemp.text = getString(R.string.deg, main.temp)
                                tvWeather.text = weather.first().description.capitalize()
                                tvHumidity.text = getString(R.string.percentage, main.humidity)
                                tvWind.text = getString(R.string.m_s, wind.speed)
                                tvPressure.text = getString(R.string.hpa, main.pressure)
                                Picasso.get().load(imageUrl(weather.first().icon)).into(ivWeatherIcon)

                                video = "${weather.first().description} $name weather"
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
        contentWelcome.visibility = View.GONE
    }

    private fun finishLoading(hasError: Boolean) {
        pbLoadCity.visibility = View.GONE
        if (hasError) {
            tvErrorCity.visibility = View.VISIBLE
            tvErrorCity.text = getString(R.string.city_not_found)
        } else {
            contentCity.visibility = View.VISIBLE
        }
    }
}
