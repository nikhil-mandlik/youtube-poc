package com.nikhil.here.youtube_poc

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject


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
    val hasYoutubePermission : Boolean = false
)

sealed class YtProfileSideEffects {

}


@HiltViewModel
class YoutubeProfileViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context
) : ViewModel(), ContainerHost<YtProfileState, YtProfileSideEffects> {


    override val container: Container<YtProfileState, YtProfileSideEffects> =
        container(YtProfileState())

    private var accessToken : String? = null

    init {
    }

    fun updateGoogleAccount(account : GoogleSignInAccount, hasYoutubePermission: Boolean) = intent {
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


}