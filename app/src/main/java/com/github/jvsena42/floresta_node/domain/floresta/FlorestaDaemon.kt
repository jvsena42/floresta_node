package com.github.jvsena42.floresta_node.domain.floresta

import android.util.Log
import com.github.jvsena42.florestadaemon.Config
import com.github.jvsena42.florestadaemon.Florestad
import com.github.jvsena42.florestadaemon.Network as FlorestaNetwork

interface FlorestaDaemon {
    suspend fun start()
    suspend fun stop()
}

class FlorestaDaemonImpl(
    private val datadir: String,
) : FlorestaDaemon {

    var isRunning = false
    private lateinit var daemon: Florestad
    override suspend fun start() {
        Log.d(TAG, "start: ")
        if (isRunning) {
            Log.d(TAG, "start: Daemon already running")
            return
        }
        try {
            Log.d(TAG, "start: datadir: $datadir")
            val config = Config(
                dataDir = datadir,
                electrumAddress = ELECTRUM_ADDRESS,
                network = FlorestaNetwork.SIGNET,
            )
            daemon = Florestad.fromConfig(config)
            daemon.start().also {
                Log.i(TAG, "start: Floresta running with config $config")
                isRunning = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "start error: ", e)
        }
    }

    override suspend fun stop() {
        if (!isRunning) return
        daemon.stop()
        isRunning = false
    }

    companion object {
        const val ELECTRUM_ADDRESS = "127.0.0.1:50001"
        private const val TAG = "FlorestaDaemonImpl"
    }
}