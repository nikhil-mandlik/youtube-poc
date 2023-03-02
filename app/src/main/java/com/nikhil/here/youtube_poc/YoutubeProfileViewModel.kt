package com.nikhil.here.youtube_poc

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.youtube.YouTubeScopes
import com.nikhil.here.youtube_poc.di.ApiService
import com.nikhil.here.youtube_poc.di.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject


@HiltViewModel
class YoutubeProfileViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context,
    private val apiService: ApiService,
    private val authService: AuthService
) : ViewModel(), ContainerHost<YtProfileState, YtProfileSideEffects> {

    companion object {
        private const val TAG = "YoutubeProfileViewModel"
    }

    override val container: Container<YtProfileState, YtProfileSideEffects> =
        container(YtProfileState())

    private var accessToken: String? = null
    private var accessTokenV2: String = "ya29.a0AVvZVsqJjtLhdVaUgKEvEKet9hg38sRnOlPur9gRqOv_HdeC-iQDWnwrqWB1qxg3t_9xmaujl4sULI1tkxQD3G2x129XrKUPjwsrVJWgmJF2lBx-YDrK7JYUYIzTYPM_QIxzhlI38LgFqMLCorNPyOndDF0caCgYKARoSARASFQGbdwaIvoZxuTSeEFek0ppg9J7znw0163"

    init {
    }

    fun updateGoogleAccount(account: GoogleSignInAccount, hasYoutubePermission: Boolean) = intent {
        val profileState = GoogleAccountState.Loaded(
            displayName = account.displayName.orEmpty(),
            profileUrl = account.photoUrl,
            email = account.email.orEmpty()
        )
        reduce {
            state.copy(
                googleAccountState = profileState,
                hasYoutubePermission = hasYoutubePermission
            )
        }
    }

    fun updateAccessToken(token: String) {
        accessToken = token
    }


    fun fetchYtActivities() {
        intent {
            try {
                val response = apiService.fetchActivities(
                    part = "snippet",
                    channelId = "UC_x5XG1OV2P6uZZ5FSM9Ttw",
                    maxResults = 10,
                    key = "AIzaSyD29qEryxgXtD_ZLnxXSTvfRc1IPZTUKUM",
                )
                Log.i(TAG, "fetchYtActivites: $response")
            } catch (e: Exception) {
                Log.i(TAG, "fetchYtActivites: $e")
            }
        }
    }

    fun fetchUserActivities() {
        intent {
            try {
                val response = apiService.fetchUserActivities(
                    part = "snippet,contentDetails",
                    maxResults = 25,
                    mine = true,
                    headers = mapOf(
                        Pair("Authorization","Bearer $accessToken")
                    )
                )
//                val response = apiService.fetchUserActivitiesWithoutHeaders(
//                    part = "snippet",
//                    maxResults = 10,
//                    mine = true,
//                    accessToken = accessToken.orEmpty()
//                )
                Log.i(TAG, "fetchUserActivities: $response")
            } catch (e: Exception) {
                Log.i(TAG, "fetchUserActivities: $e")
            }
        }
    }
    fun fetchPlayList() {
        intent {
            try {
                val response = apiService.fetchPlayLists(
                    part = "snippet,contentDetails",
                    mine = true,
                    headers = mapOf(
                        Pair("Authorization","Bearer $accessToken")
                    )
                )
//                val response = apiService.fetchUserActivitiesWithoutHeaders(
//                    part = "snippet",
//                    maxResults = 10,
//                    mine = true,
//                    accessToken = accessToken.orEmpty()
//                )
                Log.i(TAG, "fetchUserActivities: $response")
            } catch (e: Exception) {
                Log.i(TAG, "fetchUserActivities: $e")
            }
        }
    }

    fun fetchAuthInfo() {
        intent {
            try {
                val response = authService.fetchToken(
                    clientId = "446633314554-9dig39jm5fs3oikljtlpt8okga226rtd.apps.googleusercontent.com",
                    redirectUri = "com.nikhil.here.youtube_poc",
                    responseType = "code",
                    scope = YouTubeScopes.YOUTUBE_READONLY
                )
            } catch (e: Exception) {
                Log.i(TAG, "fetchAuthInfo: exception $e")
            }


        }
    }

    fun fetchTokenInfo() {
        intent {
            try {
                val response = authService.getTokenInfo(
                    token = accessToken.orEmpty()
                )
            } catch (e: Exception) {
                Log.i(TAG, "fetchTokenInfo: exception $e")
            }
        }
    }


}


sealed class GoogleAccountState {
    object NotLoaded : GoogleAccountState()
    data class Loaded(
        val displayName: String,
        val profileUrl: Uri?,
        val email: String
    ) : GoogleAccountState()
}

data class YtProfileState(
    val googleAccountState: GoogleAccountState = GoogleAccountState.NotLoaded,
    val hasYoutubePermission: Boolean = false
)

sealed class YtProfileSideEffects {

}
