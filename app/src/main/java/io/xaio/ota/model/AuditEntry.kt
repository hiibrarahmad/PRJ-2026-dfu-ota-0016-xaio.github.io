package io.xaio.ota.model

data class AuditEntry(
    val timestamp: Long,
    val deviceId: String,
    val fromVersion: String,
    val toVersion: String,
    val fromChannel: String,
    val toChannel: String,
    val direction: String,
    val epochFrom: Int,
    val epochTo: Int,
    val result: String,
    val reason: String,
)

