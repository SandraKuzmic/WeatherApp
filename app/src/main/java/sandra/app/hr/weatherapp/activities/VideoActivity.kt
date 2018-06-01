package sandra.app.hr.weatherapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import sandra.app.hr.weatherapp.R

class VideoActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoExtraKey = getString(R.string.video_extra)
        if (!intent.hasExtra(videoExtraKey)) {
            Toast.makeText(this, getString(R.string.no_data_provided), Toast.LENGTH_SHORT).show()
            finish()
        }

        val query = intent.getStringExtra(videoExtraKey)

        val fragment = supportFragmentManager.findFragmentById(R.id.videoFragment) as YouTubePlayerSupportFragment
        fragment.initialize(getString(R.string.API_KEY), this)

    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?, wasRestored: Boolean) {
        if (!wasRestored) {
            val video = "https://www.youtube.com/watch?v=aJOTlE1K90k"
            youTubePlayer?.cueVideo(video)
        }

    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, error: YouTubeInitializationResult) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
    }
}
