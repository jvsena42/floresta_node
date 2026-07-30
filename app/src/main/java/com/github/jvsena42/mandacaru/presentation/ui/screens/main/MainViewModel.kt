package com.github.jvsena42.mandacaru.presentation.ui.screens.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jvsena42.mandacaru.data.AppUpdateRepository
import com.github.jvsena42.mandacaru.data.GeoIpDatabaseRepository
import com.github.jvsena42.mandacaru.data.WalletDescriptorRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val appUpdateRepository: AppUpdateRepository,
    private val geoIpDatabaseRepository: GeoIpDatabaseRepository,
    walletDescriptorRepository: WalletDescriptorRepository,
) : ViewModel() {

    val needsDescriptor: StateFlow<Boolean> = walletDescriptorRepository.status
        .map { it.needsDescriptor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isSettingsBadgeVisible: StateFlow<Boolean> = combine(
        appUpdateRepository.updateStatus.map { it.isBadgeVisible },
        needsDescriptor,
    ) { hasUnseenUpdate, needsDescriptor -> hasUnseenUpdate || needsDescriptor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val backStack: SnapshotStateList<AppRoute> = mutableStateListOf(AppRoute.Home)

    init {
        viewModelScope.launch { appUpdateRepository.refresh(force = true) }
        // Throttled to ~30 days internally, so this is a no-op on almost every launch.
        viewModelScope.launch { geoIpDatabaseRepository.refresh() }
    }

    fun navigateTo(route: AppRoute) {
        if (backStack.lastOrNull() != route) backStack.add(route)
    }

    fun navigateBack() {
        backStack.removeLastOrNull()
    }
}
