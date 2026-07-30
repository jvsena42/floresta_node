package com.github.jvsena42.mandacaru.data

import com.github.jvsena42.mandacaru.domain.model.WalletDescriptorStatus
import kotlinx.coroutines.flow.StateFlow

interface WalletDescriptorRepository {
    val status: StateFlow<WalletDescriptorStatus>

    /** Re-reads `listdescriptors` now; call after a successful `loaddescriptor`. */
    suspend fun refresh()
}
