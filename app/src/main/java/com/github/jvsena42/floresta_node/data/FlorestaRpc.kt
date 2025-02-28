package com.github.jvsena42.floresta_node.data

import com.github.jvsena42.floresta_node.domain.model.florestaRPC.GetBlockchainInfoResponse
import com.github.jvsena42.floresta_node.domain.model.florestaRPC.GetPeerInfoResponse
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

interface FlorestaRpc {
    suspend fun getBlockchainInfo(): Flow<Result<GetBlockchainInfoResponse>>
    suspend fun rescan(): Flow<Result<JSONObject>>
    suspend fun loadDescriptor(descriptor: String): Flow<Result<JSONObject>>
    suspend fun stop(): Flow<Result<JSONObject>>
    suspend fun getPeerInfo(): Flow<Result<GetPeerInfoResponse>>
    suspend fun getTransaction(txId: String): Flow<Result<JSONObject>>
}