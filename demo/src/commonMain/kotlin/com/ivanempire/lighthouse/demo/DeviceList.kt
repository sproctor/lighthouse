package com.ivanempire.lighthouse.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.delay

@Composable
fun DeviceList(
    discoveredDeviceList: List<AbridgedMediaDevice>,
    startDiscovery: () -> Unit,
    stopDiscovery: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            items(
                items = discoveredDeviceList,
                key = { it.uuid },
            ) {
                DeviceListItem(device = it)
            }
        }

        val isDiscoveryRunning = remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    stopDiscovery()
                    isDiscoveryRunning.value = false
                },
                enabled = isDiscoveryRunning.value
            ) {
                Text(text = "Stop discovery")
            }
            Button(
                onClick = {
                    startDiscovery()
                    isDiscoveryRunning.value = true
                },
                enabled = !isDiscoveryRunning.value
            ) {
                Text(text = "Start discovery")
            }
        }
    }
}

@Composable
private fun DeviceListItem(device: AbridgedMediaDevice) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val ttl = device.cache - (currentTime - device.latestTimestamp) / 1000
    ListItem(
        headlineContent = { Text(device.location.toString()) },
        supportingContent = { Text(device.uuid) },
        trailingContent = { Text(ttl.toString()) },
    )
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
}
