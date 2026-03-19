package io.xaio.ota.model

data class DeviceVersion(
    val firmwareRev: String,
    val softwareRev: String,
    val hardwareRev: String,
    val versionCode: Int,
    val securityEpoch: Int,
    val channel: String,
) {
    companion object {
        fun fromSemver(semver: String): Int {
            val match = Regex("""(\d+)\.(\d+)\.(\d+)""").find(semver.trim()) ?: return 0
            val (major, minor, patch) = match.destructured
            return major.toInt() * 10000 + minor.toInt() * 100 + patch.toInt()
        }
    }
}

