package com.ivanempire.lighthouse.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivityViewModel(
    private val lighthouseClient: LighthouseClient,
) : ViewModel() {

    private var discoveryJob: Job? = null

    private val backingDiscoveredDevices = MutableStateFlow<List<AbridgedMediaDevice>>(emptyList())
    val discoveredDevices = backingDiscoveredDevices.asStateFlow()

    fun startDiscovery() {
        viewModelScope.launch {
            discoveryJob =
                lighthouseClient.discoverDevices()
                    .onEach { backingDiscoveredDevices.value = it }
                    .launchIn(viewModelScope)
        }
    }

    fun stopDiscovery() {
        discoveryJob?.cancel()
    }
}
