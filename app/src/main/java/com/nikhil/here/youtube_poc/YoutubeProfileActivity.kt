package com.nikhil.here.youtube_poc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.google.api.services.youtube.YouTubeScopes
import com.google.gson.Gson
import com.nikhil.here.youtube_poc.databinding.ActivityYoutubeProfileBinding
import com.nikhil.here.youtube_poc.ui.theme.YoutubepocTheme
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections


@AndroidEntryPoint
class YoutubeProfileActivity : FragmentActivity() {
    private lateinit var binding: ActivityYoutubeProfileBinding
    private var googleApiAvailability: GoogleApiAvailability? = null
    private var credential: GoogleAccountCredential? = null

    private val youtubeProfileViewModel: YoutubeProfileViewModel by viewModels()


    companion object {
        private const val TAG = "YoutubeProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YoutubepocTheme {
                val state by youtubeProfileViewModel.container.stateFlow.collectAsState()
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        initializeGooglePlayServices()
                    }) {
                        Text(text = "Initialize Google Play Services")
                    }
                }
            }
        }
    }

    private fun initializeGooglePlayServices() {
        googleApiAvailability = GoogleApiAvailability.getInstance()
        val youtubeRequestInitializer = object : YouTubeRequestInitializer() {

        }
        val credential = AppIdentityCredential(Collections.singletonList(YouTubeScopes.YOUTUBE))
        val youtube = YouTube.Builder(NetHttpTransport(), GsonFactory(), credential)
            .setYouTubeRequestInitializer(youtubeRequestInitializer)
            .build()

        //youtube.activities()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(
            TAG,
            "onActivityResult: requestCode $requestCode resultCode $resultCode data extras ${data?.extras}"
        )
        if (resultCode == RESULT_OK) {
            val selectedAccount = credential?.token
        }
    }

}