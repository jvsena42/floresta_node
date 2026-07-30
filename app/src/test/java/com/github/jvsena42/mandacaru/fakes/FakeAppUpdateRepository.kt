package com.github.jvsena42.mandacaru.fakes

import com.github.jvsena42.mandacaru.data.AppUpdateRepository
import com.github.jvsena42.mandacaru.domain.model.UpdateStatus
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAppUpdateRepository : AppUpdateRepository {
    override val updateStatus = MutableStateFlow(UpdateStatus())
    override suspend fun refresh(force: Boolean) = Unit
    override suspend fun markUpdateSeen() {
        updateStatus.value = updateStatus.value.copy(isBadgeVisible = false)
    }
}
