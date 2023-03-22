package com.nikhil.here.youtube_poc

import android.bluetooth.BluetoothClass.Device
import android.content.Context
import android.media.AudioDeviceInfo
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
class AudioDeviceInfoViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context,
) : ViewModel(), ContainerHost<AudioDeviceInfoState, AudioDeviceInfoSideEffects> {

    companion object {
        private const val TAG = "YoutubeProfileViewModel"
    }

    override val container: Container<AudioDeviceInfoState, AudioDeviceInfoSideEffects> =
        container(AudioDeviceInfoState())



    fun updateDevices(
        inputDevices : List<AudioDeviceInfo>,
        outputDevices : List<AudioDeviceInfo>,
    ) {
        intent {
            reduce {
                state.copy(
                    outputDevices = outputDevices,
                    inputDevices = inputDevices
                )
            }
        }
    }

}


data class AudioDeviceInfoState(
    val outputDevices : List<AudioDeviceInfo> = emptyList(),
    val inputDevices : List<AudioDeviceInfo> = emptyList()
)

sealed class AudioDeviceInfoSideEffects {

}
