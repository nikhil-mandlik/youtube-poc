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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTubeScopes
import com.nikhil.here.youtube_poc.databinding.ActivityYoutubeProfileBinding
import com.nikhil.here.youtube_poc.ui.theme.YoutubepocTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class YoutubeProfileActivity : FragmentActivity() {
    private lateinit var binding: ActivityYoutubeProfileBinding
    private var googleApiAvailability: GoogleApiAvailability? = null
    private var credential : GoogleAccountCredential? = null

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

        credential =
            GoogleAccountCredential.usingOAuth2(this, listOf(YouTubeScopes.YOUTUBE))

        val service = com.google.api.services.tasks.Tasks.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("youtube_poc").build()

        credential?.let {
            startActivityForResult(it.newChooseAccountIntent(), 101)

        }

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