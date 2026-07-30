package com.github.jvsena42.mandacaru.data.wallet

import com.github.jvsena42.mandacaru.domain.model.florestaRPC.response.ListDescriptorsResponse
import com.github.jvsena42.mandacaru.fakes.FakeFlorestaRpc
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WalletDescriptorRepositoryImplTest {

    private val rpc = FakeFlorestaRpc()

    @Test
    fun `an unanswered daemon never asks the user for a descriptor`() = runTest {
        rpc.listDescriptorsResults = listOf(Result.failure(IllegalStateException("booting")))
        val pollScope = TestScope(StandardTestDispatcher(testScheduler))
        val repository = WalletDescriptorRepositoryImpl(rpc, pollScope)

        testScheduler.advanceTimeBy(FIRST_POLL)
        testScheduler.runCurrent()

        assertFalse(repository.status.value.isKnown)
        assertFalse(repository.status.value.needsDescriptor)
        assertTrue("the boot window must keep retrying", rpc.listDescriptorsCallCount > 1)
        pollScope.cancel()
    }

    @Test
    fun `a single empty answer is not enough to prompt`() = runTest {
        rpc.listDescriptorsResults = listOf(empty())
        val pollScope = TestScope(StandardTestDispatcher(testScheduler))
        val repository = WalletDescriptorRepositoryImpl(rpc, pollScope)

        testScheduler.runCurrent()

        assertEquals(1, rpc.listDescriptorsCallCount)
        assertFalse(repository.status.value.needsDescriptor)
        pollScope.cancel()
    }

    @Test
    fun `two consecutive empty answers prompt for a descriptor`() = runTest {
        rpc.listDescriptorsResults = listOf(empty(), empty())
        val pollScope = TestScope(StandardTestDispatcher(testScheduler))
        val repository = WalletDescriptorRepositoryImpl(rpc, pollScope)

        testScheduler.advanceTimeBy(FIRST_POLL)
        testScheduler.runCurrent()

        assertTrue(repository.status.value.isKnown)
        assertTrue(repository.status.value.needsDescriptor)
        pollScope.cancel()
    }

    @Test
    fun `a loaded descriptor stops the prompt and the polling`() = runTest {
        rpc.listDescriptorsResults = listOf(empty(), loaded())
        val pollScope = TestScope(StandardTestDispatcher(testScheduler))
        val repository = WalletDescriptorRepositoryImpl(rpc, pollScope)

        testScheduler.advanceTimeBy(FIRST_POLL)
        testScheduler.runCurrent()
        assertEquals(listOf(DESCRIPTOR), repository.status.value.descriptors)
        assertFalse(repository.status.value.needsDescriptor)

        val callsAfterLoad = rpc.listDescriptorsCallCount
        testScheduler.advanceTimeBy(LONG_IDLE)
        testScheduler.runCurrent()

        assertEquals(callsAfterLoad, rpc.listDescriptorsCallCount)
        pollScope.cancel()
    }

    @Test
    fun `refresh picks up a descriptor loaded out of band`() = runTest {
        rpc.listDescriptorsResults = listOf(empty(), empty(), loaded())
        val pollScope = TestScope(StandardTestDispatcher(testScheduler))
        val repository = WalletDescriptorRepositoryImpl(rpc, pollScope)

        testScheduler.advanceTimeBy(FIRST_POLL)
        testScheduler.runCurrent()
        assertTrue(repository.status.value.needsDescriptor)

        repository.refresh()

        assertEquals(listOf(DESCRIPTOR), repository.status.value.descriptors)
        assertFalse(repository.status.value.needsDescriptor)
        pollScope.cancel()
    }

    private fun empty() = Result.success(response(emptyList()))

    private fun loaded() = Result.success(response(listOf(DESCRIPTOR)))

    private fun response(descriptors: List<String>) =
        ListDescriptorsResponse(id = 1, jsonrpc = "2.0", result = descriptors)

    private companion object {
        const val DESCRIPTOR = "wpkh([73c5da0a/84h/1h/0h]tpubDC8msFGeGuwnKG9Upg7DM2b4/0/*)"
        const val FIRST_POLL = 4_000L
        const val LONG_IDLE = 120_000L
    }
}
