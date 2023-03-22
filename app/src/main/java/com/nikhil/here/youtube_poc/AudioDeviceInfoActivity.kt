package com.nikhil.here.youtube_poc

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nikhil.here.youtube_poc.ui.theme.YoutubepocTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalFoundationApi::class)
@AndroidEntryPoint
class AudioDeviceInfoActivity : AppCompatActivity() {

    private val viewModel by viewModels<AudioDeviceInfoViewModel>()
    private lateinit var audioManager: AudioManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YoutubepocTheme {
                Surface {
                    val state by viewModel.container.stateFlow.collectAsState()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        stickyHeader {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(onClick = {
                                    fetchDevices()
                                }) {
                                    Text(text = "Refresh Devices")
                                }
                            }
                        }

                        stickyHeader {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Input Devices")
                            }
                        }

                        items(state.inputDevices) { item ->
                            AudioDeviceInfoUi(info = item)
                        }

                        stickyHeader {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Output Devices")
                            }
                        }

                        items(state.outputDevices) { item ->
                            AudioDeviceInfoUi(info = item)
                        }
                    }
                }

            }
        }
        initializeAudioManager()
    }

    private fun initializeAudioManager() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        fetchDevices()
    }

    private fun fetchDevices() {
        val outputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val inputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
        viewModel.updateDevices(
            inputDevices = inputDevices.toList(),
            outputDevices = outputDevices.toList()
        )
    }

}


@Composable
fun AudioDeviceInfoUi(info: AudioDeviceInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .border(BorderStroke(1.dp, Color.Black))
            .padding(6.dp)
    ) {
        Text(text = "Product Name : ${info.productName}")
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "type : ${info.type}")
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "address : ${info.address}")
    }
    Spacer(modifier = Modifier.height(6.dp))
}