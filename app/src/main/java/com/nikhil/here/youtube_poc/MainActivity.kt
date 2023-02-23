package com.nikhil.here.youtube_poc

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.createLifecycleAwareWindowRecomposer
import com.google.android.youtube.player.YouTubeApiServiceUtil
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.nikhil.here.youtube_poc.databinding.ActivityMainBinding

class MainActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private var youtubePlayer: YouTubePlayer? = null

    private val ytVideoIds = listOf(
        "-hsdXiwA4c0"
    )

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()

        // checking whether the youtube service avilable or not
        val player2 = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this)

    }

    private fun initListeners() {
        binding.btnInitialize.setOnClickListener {
            if (youtubePlayer != null) {
                Toast.makeText(this, "Player is already initialized", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            initializeYoutubePlayerView()
        }

        binding.btnPlayPause.setOnClickListener {
            if (youtubePlayer == null) {
                Toast.makeText(this, "Player not initialised", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            playYtVideo()
        }

        binding.btnForward.setOnClickListener {
            forwardPlayer()
        }

        binding.btnRewind.setOnClickListener {
            rewindPlayer()
        }
    }

    private fun forwardPlayer() {
        youtubePlayer?.let {
            it.seekToMillis((it.currentTimeMillis + 10 * 1000).coerceIn(minimumValue = 0, maximumValue = it.durationMillis))
        }
    }

    private fun rewindPlayer() {
        youtubePlayer?.let {
            it.seekToMillis((it.currentTimeMillis - 10 * 1000).coerceIn(minimumValue = 0, maximumValue = it.durationMillis))
        }
    }

    private fun playYtVideo() {
        youtubePlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                if (it.currentTimeMillis == 0) {
                    it.loadVideos(ytVideoIds)
                    it.play()
                } else {
                    it.play()
                }
            }
        }
    }


    private fun initializeYoutubePlayerView() {
        val fullScreenListener = YouTubePlayer.OnFullscreenListener {
            Log.i(TAG, "onFullscreen: $it")
        }

        val playbackEventListener = object : YouTubePlayer.PlaybackEventListener {
            override fun onPlaying() {
                Log.i(TAG, "onPlaying: ")
            }

            override fun onPaused() {
                Log.i(TAG, "onPaused: ")
            }

            override fun onStopped() {
                Log.i(TAG, "onStopped: ")
            }

            override fun onBuffering(p0: Boolean) {
                Log.i(TAG, "onBuffering: $p0")
            }

            override fun onSeekTo(p0: Int) {
                Log.i(TAG, "onSeekTo: $p0")
            }
        }

        val playerStateChangeListener = object : YouTubePlayer.PlayerStateChangeListener {
            override fun onLoading() {
                Log.i(TAG, "onLoading: ")
            }

            override fun onLoaded(p0: String?) {
                Log.i(TAG, "onLoaded: $p0")
            }

            override fun onAdStarted() {
                Log.i(TAG, "onAdStarted: ")
            }

            override fun onVideoStarted() {
                Log.i(TAG, "onVideoStarted: ")
            }

            override fun onVideoEnded() {
                Log.i(TAG, "onVideoEnded: ")
            }

            override fun onError(p0: YouTubePlayer.ErrorReason?) {
                Log.i(TAG, "onError: $p0")
            }
        }

        val playListEventListener = object : YouTubePlayer.PlaylistEventListener {
            override fun onPrevious() {
                Log.i(TAG, "onPrevious: ")
            }

            override fun onNext() {
                Log.i(TAG, "onNext: ")
            }

            override fun onPlaylistEnded() {
                Log.i(TAG, "onPlaylistEnded: ")
            }
        }


        val youtubeListener = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                wasRestored: Boolean
            ) {
                Log.i(
                    TAG,
                    "onInitializationSuccess: provider $provider , player : $player, player : $player"
                )
                youtubePlayer = player?.apply {
                    setOnFullscreenListener(fullScreenListener)
                    setPlaybackEventListener(playbackEventListener)
                    setPlayerStateChangeListener(playerStateChangeListener)
                    setPlaylistEventListener(playListEventListener)
                    fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT
                    setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                    setShowFullscreenButton(false)
                }
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider?,
                result: YouTubeInitializationResult?
            ) {
                Log.i(TAG, "onInitializationFailure: provider $provider, result $result")
                youtubePlayer = null
            }
        }

        binding.viewYoutubePlayer.initialize(
            YoutubePlayerConfig.API_KEY,
            youtubeListener
        )
    }


}
