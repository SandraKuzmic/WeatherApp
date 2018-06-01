package sandra.job.hr.weatherapp.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import kotlinx.android.synthetic.main.activity_search.*
import sandra.job.hr.weatherapp.R


class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if(Intent.ACTION_SEARCH == intent.action) {
            preformSearch(intent.getStringExtra(SearchManager.QUERY))
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
        tv_hello.text = query
    }
}
