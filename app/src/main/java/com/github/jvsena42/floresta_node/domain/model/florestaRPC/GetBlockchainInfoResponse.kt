package com.github.jvsena42.floresta_node.domain.model.florestaRPC


import com.google.gson.annotations.SerializedName

data class GetBlockchainInfoResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("jsonrpc")
    val jsonrpc: String,
    @SerializedName("result")
    val result: Result
)

data class Result(
    @SerializedName("best_block")
    val bestBlock: String,
    @SerializedName("chain")
    val chain: String,
    @SerializedName("difficulty")
    val difficulty: Float,
    @SerializedName("height")
    val height: Int,
    @SerializedName("ibd")
    val ibd: Boolean,
    @SerializedName("latest_block_time")
    val latestBlockTime: Int,
    @SerializedName("latest_work")
    val latestWork: String,
    @SerializedName("leaf_count")
    val leafCount: Int,
    @SerializedName("progress")
    val progress: Float,
    @SerializedName("root_count")
    val rootCount: Int,
    @SerializedName("root_hashes")
    val rootHashes: List<String>,
    @SerializedName("validated")
    val validated: Int
)