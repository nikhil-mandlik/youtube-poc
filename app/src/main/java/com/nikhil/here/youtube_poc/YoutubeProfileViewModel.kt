package com.nikhil.here.youtube_poc

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.services.youtube.YouTubeScopes
import com.nikhil.here.youtube_poc.data.PlayListResponse
import com.nikhil.here.youtube_poc.di.ApiService
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
    private val apiService: ApiService
) : ViewModel(), ContainerHost<YtProfileState, YtProfileSideEffects> {

    companion object {
        private const val TAG = "YoutubeProfileViewModel"
    }

    override val container: Container<YtProfileState, YtProfileSideEffects> =
        container(YtProfileState())

    private var accessToken: String? = null


    fun updateGoogleAccount(account: GoogleSignInAccount) = intent {
        val hasYtPermission = GoogleSignIn.hasPermissions(
            account,
            Scope(YouTubeScopes.YOUTUBE_READONLY)
        )
        reduce {
            state.copy(
                displayName = account.displayName ?: Constants.MojDisplayName,
                profileUrl = account.photoUrl ?: Constants.MojProfileUri.toUri(),
                email = account.email ?: Constants.MojEmail,
                hasYoutubePermission = hasYtPermission,
                isExpired = account.isExpired,
                isVerified = true
            )
        }
    }

    fun updateAccessToken(token: String) {
        accessToken = token
    }

    fun fetchUserPlayList() {
        intent {
            try {
                val response = apiService.fetchUserPlaylists(
                    part = "snippet,contentDetails",
                    mine = true,
                    headers = mapOf(
                        Pair("Authorization", "Bearer $accessToken")
                    ),
                    maxResults = 25
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.i(TAG, "fetchDefaultPlayLists: $it")
                        reduce {
                            state.copy(
                                items = it.items
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i(TAG, "fetchUserActivities: $e")
            }
        }
    }

    fun fetchDefaultPlayLists() = intent {
        try {
            val response = apiService.fetchDefaultPlayLists(
                part = "snippet,contentDetails",
                channelId = "UCu_J99iN4rpk6D2vaQgTcAg",
                key = Constants.ApiKey,
                maxResults = 25
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i(TAG, "fetchDefaultPlayLists: $it")
                    reduce {
                        state.copy(
                            items = it.items
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, "fetchUserActivities: $e")
        }
    }
}


data class YtProfileState(
    val displayName: String = Constants.MojDisplayName,
    val profileUrl: Uri? = Constants.MojProfileUri.toUri(),
    val email: String = Constants.MojEmail,
    val hasYoutubePermission: Boolean = false,
    val isExpired: Boolean = true,
    val isVerified: Boolean = false,
    val items : List<PlayListResponse.PlayListItem> = emptyList()
)

sealed class YtProfileSideEffects {

}
