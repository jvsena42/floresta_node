package com.github.jvsena42.mandacaru.data.wallet

import android.util.Log
import com.github.jvsena42.mandacaru.data.FlorestaRpc
import com.github.jvsena42.mandacaru.data.WalletDescriptorRepository
import com.github.jvsena42.mandacaru.domain.model.WalletDescriptorStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class WalletDescriptorRepositoryImpl(
    private val florestaRpc: FlorestaRpc,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : WalletDescriptorRepository {

    private val _status = MutableStateFlow(WalletDescriptorStatus())
    override val status = _status.asStateFlow()

    private var emptyResponses = 0

    init {
        scope.launch { pollUntilLoaded() }
    }

    override suspend fun refresh() = fetch()

    /**
     * Floresta has no "unload descriptor" RPC, so a non-empty answer is terminal and the loop
     * exits for good. While the wallet is still empty it keeps polling — slowly, once the
     * daemon has clearly finished booting — so a descriptor loaded out of band still lands.
     */
    private suspend fun pollUntilLoaded() {
        var attempts = 0
        while (!_status.value.hasDescriptors) {
            fetch()
            if (_status.value.hasDescriptors) return
            attempts++
            delay(if (attempts < FAST_POLL_ATTEMPTS) FAST_POLL_INTERVAL else SLOW_POLL_INTERVAL)
        }
    }

    private suspend fun fetch() {
        val result = florestaRpc.listDescriptors().firstOrNull() ?: return
        result
            .onFailure { error -> Log.d(TAG, "fetch: listdescriptors unavailable", error) }
            .onSuccess { response ->
                if (response.result.isNotEmpty()) {
                    _status.value = WalletDescriptorStatus(response.result, isKnown = true)
                    return
                }
                emptyResponses++
                _status.value = WalletDescriptorStatus(
                    descriptors = emptyList(),
                    isKnown = emptyResponses >= EMPTY_CONFIRMATIONS,
                )
            }
    }

    companion object {
        private const val TAG = "WalletDescriptorRepo"
        private const val FAST_POLL_ATTEMPTS = 10
        private val FAST_POLL_INTERVAL = 3.seconds
        private val SLOW_POLL_INTERVAL = 30.seconds

        /**
         * One empty answer can arrive before Floresta finishes deserialising a persisted wallet,
         * so require two before telling the UI the wallet is empty.
         */
        private const val EMPTY_CONFIRMATIONS = 2
    }
}
