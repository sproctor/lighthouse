package com.ivanempire.lighthouse.demo


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ivanempire.lighthouse.lighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun main() {
    val lighthouseClient = lighthouseClient()

    var discoveryJob: Job? = null

    application {
        Window(
            title = "Lighthouse Demo",
            onCloseRequest = ::exitApplication
        ) {
            val devicesState = mutableStateOf<List<AbridgedMediaDevice>>(emptyList())
            val scope = rememberCoroutineScope()

            Surface(modifier = Modifier.fillMaxSize()) {
                DeviceList(
                    discoveredDeviceList = devicesState.value,
                    startDiscovery = {
                        discoveryJob = scope.launch {
                            lighthouseClient.discoverDevices()
                                .collect { devices ->
                                    devicesState.value = devices.sortedBy { it.uuid }
                                }
                        }
                    },
                    stopDiscovery = {
                        discoveryJob?.cancel()
                    }
                )
            }
        }
    }
}