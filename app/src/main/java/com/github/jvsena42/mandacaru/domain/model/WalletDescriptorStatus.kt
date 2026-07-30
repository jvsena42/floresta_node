package com.github.jvsena42.mandacaru.domain.model

/**
 * [isKnown] separates "the daemon has not answered `listdescriptors` yet" from "the daemon
 * answered and the wallet is empty". Only the latter may surface the add-a-descriptor prompts,
 * so they never flash while the RPC server is still booting.
 */
data class WalletDescriptorStatus(
    val descriptors: List<String> = emptyList(),
    val isKnown: Boolean = false,
) {
    val hasDescriptors: Boolean get() = descriptors.isNotEmpty()

    val needsDescriptor: Boolean get() = isKnown && descriptors.isEmpty()
}
