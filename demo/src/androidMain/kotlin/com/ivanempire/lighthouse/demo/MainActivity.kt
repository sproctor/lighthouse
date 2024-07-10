package com.ivanempire.lighthouse.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.lighthouseClient

class MainActivity : ComponentActivity() {

    private lateinit var lighthouseClient: LighthouseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Example implementation of a custom logging system
        val customLogger =
            object : LighthouseLogger() {
                override fun logStateMessage(tag: String, message: String) {
                    Log.d(tag, message)
                }

                override fun logStatusMessage(tag: String, message: String) {
                    Log.d(tag, message)
                }

                override fun logPacketMessage(tag: String, message: String) {
                    Log.d(tag, message)
                }

                override fun logErrorMessage(tag: String, message: String, ex: Throwable?) {
                    Log.e(tag, message, ex)
                }
            }

        // Setup the client
        lighthouseClient =
            lighthouseClient(this) {
                logger = customLogger
                retryCount = 2
            }

        // Skips the need for Dagger in a simple demo app
        val viewModelFactory = MainActivityViewModelFactory(lighthouseClient)
        val viewModel = viewModelFactory.create(MainActivityViewModel::class.java)

        setContent {
            val discoveredDeviceList by viewModel.discoveredDevices.collectAsState()
            DeviceList(
                discoveredDeviceList = discoveredDeviceList,
                startDiscovery = viewModel::startDiscovery,
                stopDiscovery = viewModel::stopDiscovery,
                getDetails = {
                    lighthouseClient.retrieveDescription(it)
                }
            )
        }
    }
}
