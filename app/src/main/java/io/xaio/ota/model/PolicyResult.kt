package io.xaio.ota.model

sealed class PolicyResult {
    data object Allow : PolicyResult()
    data object AlreadyInstalled : PolicyResult()
    data class DowngradeWarning(
        val fromVersion: String,
        val toVersion: String,
        val requiresCheckbox: Boolean,
    ) : PolicyResult()
    data class HardBlock(val reason: String) : PolicyResult()
    data class ForcedUpdate(val toVersion: String) : PolicyResult()
}

