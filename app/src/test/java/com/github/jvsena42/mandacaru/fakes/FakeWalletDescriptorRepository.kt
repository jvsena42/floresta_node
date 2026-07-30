package com.github.jvsena42.mandacaru.fakes

import com.github.jvsena42.mandacaru.data.WalletDescriptorRepository
import com.github.jvsena42.mandacaru.domain.model.WalletDescriptorStatus
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWalletDescriptorRepository : WalletDescriptorRepository {
    override val status = MutableStateFlow(WalletDescriptorStatus())

    var refreshCount = 0
        private set

    override suspend fun refresh() {
        refreshCount++
    }
}
