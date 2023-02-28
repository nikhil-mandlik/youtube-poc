package com.nikhil.here.youtube_poc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.nikhil.here.youtube_poc.databinding.ActivityYoutubeProfileBinding
import com.nikhil.here.youtube_poc.ui.theme.YoutubepocTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


@AndroidEntryPoint
class YoutubeProfileActivity : FragmentActivity() {
    private lateinit var binding: ActivityYoutubeProfileBinding
    private val youtubeProfileViewModel: YoutubeProfileViewModel by viewModels()
    private var mGoogleSignInClient: GoogleSignInClient? = null

    companion object {
        private const val TAG = "YoutubeProfileActivity"
        private const val RC_YOUTUBE_PERMISSION = 102
    }


    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
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

                    when (val profile = state.googleAccountState) {
                        is GoogleAccountState.Loaded -> {
                            AsyncImage(
                                model = profile.profileUrl,
                                contentDescription = "Profile Image",
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "profile: ${profile.displayName}",
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = profile.email, textAlign = TextAlign.Center)


                            if (state.hasYoutubePermission.not()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    fetchYoutubeProfile()
                                }) {
                                    Text(text = "Fetch Youtube Profile")
                                }
                            }
                        }

                        GoogleAccountState.NotLoaded -> {
                            AndroidView(
                                factory = {
                                    SignInButton(it)
                                }, update = {
                                    it.setOnClickListener {
                                        initiateGoogleSignIn()
                                    }
                                }
                            )
                        }
                    }
                    Divider()
                    YoutubeFeed()
                }
            }
        }
        initializeGooglePlayServices()
    }


    @Composable
    fun YoutubeFeed() {

    }

    private fun initializeGooglePlayServices() {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        lastSignedInAccount?.let {
            val hasYoutubePermission = GoogleSignIn.hasPermissions(
                it,
                Scope(YouTubeScopes.YOUTUBE_READONLY)
            )
            youtubeProfileViewModel.updateGoogleAccount(
                account = it,
                hasYoutubePermission = hasYoutubePermission
            )
            if (hasYoutubePermission) {
                fetchYoutubeFeed()
            }
            //val token = GoogleAccountCredential.usingOAuth2(this, listOf(YouTubeScopes.YOUTUBE_READONLY)).token
            //Log.i(TAG, "initializeGooglePlayServices: token $token")
            lastSignedInAccount.account?.let { account ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val scope = "oauth2:${Scope(YouTubeScopes.YOUTUBE_READONLY).scopeUri}"
                        val token = GoogleAuthUtil.getToken(
                            this@YoutubeProfileActivity,
                            account,
                            scope
                        )
                        Log.i(TAG, "initializeGooglePlayServices: token $token")
                        youtubeProfileViewModel.updateAccessToken(token)
                    } catch (e : Exception) {
                        Log.i(TAG, "initializeGooglePlayServices: ${e.localizedMessage}")
                    }
                }

            }
        }
    }

    private fun fetchYoutubeFeed() {
    }

    private fun initiateGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (lastSignedInAccount == null) {
            mGoogleSignInClient?.signInIntent?.let {
                startForResult.launch(it)
            }
        }
    }

    private fun fetchYoutubeProfile() {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this) ?: return
        val hasPermission =
            GoogleSignIn.hasPermissions(lastSignedInAccount, Scope(YouTubeScopes.YOUTUBE_READONLY))
        if (hasPermission.not()) {
            GoogleSignIn.requestPermissions(
                this, RC_YOUTUBE_PERMISSION, lastSignedInAccount,
                Scope(YouTubeScopes.YOUTUBE_READONLY)
            )
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            initializeGooglePlayServices()
        } catch (e: ApiException) {
            Log.i(
                TAG,
                "handleSignInResult exception : status ${e.status} statusCode ${e.statusCode} localizedMessage ${e.localizedMessage}"
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "onActivityResult: requestCode $requestCode resultCode $resultCode data $data")
        if (requestCode == RC_YOUTUBE_PERMISSION && resultCode == RESULT_OK) {
            initializeGooglePlayServices()
        }
    }

}