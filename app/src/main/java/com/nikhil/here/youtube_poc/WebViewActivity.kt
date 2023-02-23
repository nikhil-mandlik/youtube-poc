package com.nikhil.here.youtube_poc

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.ComponentActivity
import com.nikhil.here.youtube_poc.databinding.ActivityWebViewBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import org.json.JSONObject

class WebViewActivity : ComponentActivity() {
    private lateinit var binding: ActivityWebViewBinding
    private var palyerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN

    companion object {
        private const val TAG = "WebViewActivity"
    }

    private val ytVideoIds = listOf(
        "-hsdXiwA4c0"
    )

    private var ytPlayer: YouTubePlayer? = null
    private var currentMilliSecond: Float = 0f
    private var duration: Float = 0f

    private val ytPlayerListener = object : YouTubePlayerListener {
        override fun onApiChange(youTubePlayer: YouTubePlayer) {
            Log.i(TAG, "onApiChange: ")
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            currentMilliSecond = second
        }

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            Log.i(TAG, "onError: $error")
        }

        override fun onPlaybackQualityChange(
            youTubePlayer: YouTubePlayer,
            playbackQuality: PlayerConstants.PlaybackQuality
        ) {
            Log.i(TAG, "onPlaybackQualityChange: quality $playbackQuality")
        }

        override fun onPlaybackRateChange(
            youTubePlayer: YouTubePlayer,
            playbackRate: PlayerConstants.PlaybackRate
        ) {
            Log.i(TAG, "onPlaybackRateChange: $playbackRate")
        }

        override fun onReady(youTubePlayer: YouTubePlayer) {
            Log.i(TAG, "onReady: ")
            ytPlayer = youTubePlayer.apply {
                loadVideo(ytVideoIds.first(), 0.0f)
            }
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            Log.i(TAG, "onStateChange: $state")
            palyerState = state
        }

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            Log.i(TAG, "onVideoDuration: $duration")
            this@WebViewActivity.duration = duration
        }

        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
            Log.i(TAG, "onVideoId: $videoId")
        }

        override fun onVideoLoadedFraction(
            youTubePlayer: YouTubePlayer,
            loadedFraction: Float
        ) {
//            Log.i(TAG, "onVideoLoadedFraction: $loadedFraction")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(binding.ytPlayerView)

        val options = IFramePlayerOptions.Builder()
            .controls(0)
            .rel(0)
            .ivLoadPolicy(3)
            .ccLoadPolicy(0)
            .build()

        binding.ytPlayerView.initialize(
            youTubePlayerListener = ytPlayerListener,
            playerOptions = options
        )

        binding.btnPlayPause.setOnClickListener {
            when (palyerState) {
                PlayerConstants.PlayerState.PLAYING -> {
                    ytPlayer?.pause()
                }
                else -> {
                    ytPlayer?.play()
                }
            }
        }
        
        binding.seekbarVolumeController.setOnSeekBarChangeListener (object  : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged: volume : $progress")
                ytPlayer?.setVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
               
            }
        })

        (getSystemService(AUDIO_SERVICE) as AudioManager)?.let {
            val current = it.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            Log.i(TAG, "onStartTrackingTouch: current $current max $max")
            binding.seekbarVolumeController.setProgress((current / max) * 100)
        }

        binding.seekbarDurationController.setOnSeekBarChangeListener(object  : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val seekDuration = (progress * duration) / 100
                ytPlayer?.seekTo(seekDuration)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

       

    }
}