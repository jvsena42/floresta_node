package com.github.jvsena42.mandacaru.presentation.ui.screens.main

import com.github.jvsena42.mandacaru.domain.model.UpdateStatus
import com.github.jvsena42.mandacaru.domain.model.WalletDescriptorStatus
import com.github.jvsena42.mandacaru.fakes.FakeAppUpdateRepository
import com.github.jvsena42.mandacaru.fakes.FakeGeoIpDatabaseRepository
import com.github.jvsena42.mandacaru.fakes.FakeWalletDescriptorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * The Settings tab carries one badge dot for two independent reasons — an unseen app update
 * and a missing wallet descriptor — so clearing either one alone must not clear the dot.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var appUpdateRepository: FakeAppUpdateRepository
    private lateinit var walletDescriptorRepository: FakeWalletDescriptorRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        appUpdateRepository = FakeAppUpdateRepository()
        walletDescriptorRepository = FakeWalletDescriptorRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): MainViewModel {
        val vm = MainViewModel(
            appUpdateRepository = appUpdateRepository,
            geoIpDatabaseRepository = FakeGeoIpDatabaseRepository(),
            walletDescriptorRepository = walletDescriptorRepository,
        )
        dispatcher.scheduler.runCurrent()
        return vm
    }

    @Test
    fun `no update and no missing descriptor means no badge`() {
        val vm = buildViewModel()

        assertFalse(vm.isSettingsBadgeVisible.value)
        assertFalse(vm.needsDescriptor.value)
    }

    @Test
    fun `an unseen update alone shows the badge`() {
        val vm = buildViewModel()

        appUpdateRepository.updateStatus.value = UpdateStatus(isBadgeVisible = true)
        dispatcher.scheduler.runCurrent()

        assertTrue(vm.isSettingsBadgeVisible.value)
        assertFalse(vm.needsDescriptor.value)
    }

    @Test
    fun `a missing descriptor alone shows the badge`() {
        val vm = buildViewModel()

        walletDescriptorRepository.status.value = WalletDescriptorStatus(isKnown = true)
        dispatcher.scheduler.runCurrent()

        assertTrue(vm.needsDescriptor.value)
        assertTrue(vm.isSettingsBadgeVisible.value)
    }

    @Test
    fun `marking the update seen keeps the badge while a descriptor is still missing`() {
        val vm = buildViewModel()
        appUpdateRepository.updateStatus.value = UpdateStatus(isBadgeVisible = true)
        walletDescriptorRepository.status.value = WalletDescriptorStatus(isKnown = true)
        dispatcher.scheduler.runCurrent()
        assertTrue(vm.isSettingsBadgeVisible.value)

        appUpdateRepository.updateStatus.value = UpdateStatus(isBadgeVisible = false)
        dispatcher.scheduler.runCurrent()

        assertTrue(vm.isSettingsBadgeVisible.value)
    }

    @Test
    fun `loading a descriptor clears the badge once the update is also seen`() {
        val vm = buildViewModel()
        walletDescriptorRepository.status.value = WalletDescriptorStatus(isKnown = true)
        dispatcher.scheduler.runCurrent()

        walletDescriptorRepository.status.value =
            WalletDescriptorStatus(descriptors = listOf("wpkh(xpub…)"), isKnown = true)
        dispatcher.scheduler.runCurrent()

        assertFalse(vm.needsDescriptor.value)
        assertFalse(vm.isSettingsBadgeVisible.value)
    }
}
