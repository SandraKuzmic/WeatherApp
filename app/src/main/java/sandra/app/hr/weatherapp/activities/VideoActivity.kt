package sandra.app.hr.weatherapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import sandra.app.hr.weatherapp.R


class VideoActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener {

    private lateinit var videoId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoExtraKey = getString(R.string.video_extra)
        if (!intent.hasExtra(videoExtraKey)) {
            Toast.makeText(this, getString(R.string.no_data_provided), Toast.LENGTH_SHORT).show()
            finish()
        }

        val query = intent.getStringExtra(videoExtraKey)

        Observable.create(ObservableOnSubscribe<String> {
            startYouTubeSearch(query)?.let { result -> it.onNext(result) }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onComplete() {
                    }

                    override fun onNext(video: String) {
                        Toast.makeText(this@VideoActivity, video, Toast.LENGTH_LONG).show()
                        videoId = video
                        val fragment = supportFragmentManager.findFragmentById(R.id.videoFragment) as YouTubePlayerSupportFragment
                        fragment.initialize(getString(R.string.API_KEY), this@VideoActivity)
                    }

                    override fun onError(e: Throwable) {
                        Log.d("OkHttp", e.localizedMessage)
                    }

                })

    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?, wasRestored: Boolean) {
        if (!wasRestored) {
            youTubePlayer?.cueVideo(videoId)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, error: YouTubeInitializationResult) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
    }

    private fun startYouTubeSearch(query: String): String? {
        val youTube = YouTube.Builder(NetHttpTransport(), JacksonFactory(), HttpRequestInitializer { })
                .setApplicationName(getString(R.string.app_name))
                .build()

        val search = youTube.search().list("id,snippet")
        search.key = getString(R.string.API_KEY)
        search.q = query
        search.type = "video"
        search.fields = "items(id/kind,id/videoId,snippet/title,snippet/publishedAt,snippet/thumbnails/default/url),nextPageToken"
        search.maxResults = 1

        return search.execute().items.first().id.videoId
    }
}

