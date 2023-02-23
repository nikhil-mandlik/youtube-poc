package com.nikhil.here.youtube_poc

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject


data class YtProfileState(
    val isGooglePlayServiceAvailable: Boolean = false
)

sealed class YtProfileSideEffects {

}


@HiltViewModel
class YoutubeProfileViewModel @Inject constructor() : ViewModel(), ContainerHost<YtProfileState, YtProfileSideEffects> {


    override val container: Container<YtProfileState, YtProfileSideEffects> =
        container(YtProfileState())


}