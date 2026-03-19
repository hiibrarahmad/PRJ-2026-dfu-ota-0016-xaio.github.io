package io.xaio.ota.model

data class PendingConfirmation(
    val release: ReleaseRecord,
    val title: String,
    val message: String,
    val reason: String,
    val requiresCheckbox: Boolean = false,
    val checkboxLabel: String = "",
    val dismissible: Boolean = true,
)

data class OtaUiState(
    val deviceAddress: String = "",
    val selectedChannel: String = "stable",
    val deviceVersion: DeviceVersion? = null,
    val latestRelease: ReleaseRecord? = null,
    val releases: List<ReleaseRecord> = emptyList(),
    val busyMessage: String? = null,
    val downloadProgress: Int? = null,
    val flashProgress: Int? = null,
    val statusMessage: String = "Enter the device MAC address, then read the current firmware.",
    val errorMessage: String? = null,
    val pendingConfirmation: PendingConfirmation? = null,
    val auditExportPath: String? = null,
)

