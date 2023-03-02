package com.nikhil.here.youtube_poc

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.youtube.YouTubeScopes
import com.nikhil.here.youtube_poc.ui.UserProfile
import com.nikhil.here.youtube_poc.ui.YoutubeFeed
import com.nikhil.here.youtube_poc.ui.theme.YoutubepocTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class YoutubeProfileActivity : FragmentActivity() {
    private val ytViewModel: YoutubeProfileViewModel by viewModels()
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mGoogleSignInOptions: GoogleSignInOptions? = null
    private var mToken: String = ""

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
                val state by ytViewModel.container.stateFlow.collectAsState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserProfile(state)
                    Divider(modifier = Modifier.padding(16.dp))
                    if (state.isVerified.not() || state.hasYoutubePermission.not()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (state.isVerified.not()) {
                                Button(onClick = {
                                    initiateGoogleSignIn()
                                }) {
                                    Text(text = "Google Sign In")
                                }
                            }
                            if (state.hasYoutubePermission.not()) {
                                Button(
                                    onClick = {
                                        requestYtPermission()
                                    }
                                ) {
                                    Text(text = "Req YT Permission")
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(16.dp))
                    }

                    if (state.isVerified) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (state.isExpired) {
                                Button(onClick = {
                                    silentSignIn()
                                }) {
                                    Text(text = "Silent Sign In")
                                }
                            }
                            Button(
                                onClick = {
                                    invalidateAndFetchNewToken()
                                }
                            ) {
                                Text(text = "Invalidate Token")
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                ytViewModel.fetchDefaultPlayLists()
                            }) {
                                Text(text = "Fetch Default")
                            }
                            Button(onClick = {
                                ytViewModel.fetchUserPlayList()
                            }) {
                                Text(text = "Fetch User")
                            }
                        }
                        Divider(modifier = Modifier.padding(16.dp))
                    }

                    YoutubeFeed(state.items)
                }
            }
        }
        initGooglePlayServices()
    }

    private fun invalidateAndFetchNewToken() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                GoogleAuthUtil.clearToken(this@YoutubeProfileActivity, mToken)
                val lastSignedInAccount =
                    GoogleSignIn.getLastSignedInAccount(this@YoutubeProfileActivity)
                lastSignedInAccount?.account?.let { fetchToken(it) }
            } catch (e: Exception) {
                Log.i(TAG, "invalidateAndFetchNewToken: exception $e")
            }
        }
    }

    private fun initGooglePlayServices() = lifecycleScope.launch(Dispatchers.IO) {
        GoogleSignIn.getLastSignedInAccount(this@YoutubeProfileActivity)?.let {
            Log.i(TAG, "initGooglePlayServices: found last signed in account")
            ytViewModel.updateGoogleAccount(account = it)
            it.account?.let { account ->
                Log.i(TAG, "initGooglePlayServices: fetching token for account ${account.name}")
                fetchToken(account = account)
            }
        }
    }

    private fun fetchToken(account: Account) = lifecycleScope.launch(Dispatchers.IO) {
        try {
            val scope = "oauth2:${Scope(YouTubeScopes.YOUTUBE_READONLY).scopeUri}"
            val token = GoogleAuthUtil.getToken(
                this@YoutubeProfileActivity,
                account,
                scope
            )
            mToken = token
            Log.i(TAG, "fetchToken: token $token")
            ytViewModel.updateAccessToken(token)
        } catch (e: Exception) {
            Log.i(TAG, "fetchToken exception :  ${e.localizedMessage}")
        }
    }


    private fun initiateGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions!!)
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (lastSignedInAccount == null) {
            mGoogleSignInClient?.signInIntent?.let {
                startForResult.launch(it)
            }
        }
    }

    private fun silentSignIn() {
        try {
            val gso = mGoogleSignInOptions
                ?: GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            val client = mGoogleSignInClient ?: GoogleSignIn.getClient(this, gso)
            val task = client.silentSignIn()
            task.addOnSuccessListener {
                ytViewModel.updateGoogleAccount(it)
                it.account?.let { account ->
                    fetchToken(account = account)
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, "silentSignIn: exception $e")
        }
    }

    private fun requestYtPermission() {
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            val hasPermission =
                GoogleSignIn.hasPermissions(it, Scope(YouTubeScopes.YOUTUBE_READONLY))
            if (hasPermission.not()) {
                GoogleSignIn.requestPermissions(
                    this, RC_YOUTUBE_PERMISSION, it,
                    Scope(YouTubeScopes.YOUTUBE_READONLY)
                )
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            initGooglePlayServices()
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
            initGooglePlayServices()
        }
    }

}