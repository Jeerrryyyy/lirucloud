package de.liruhg.lirucloud.library.proxy

data class ProxyInformation(
    val joinPower: Int,
    val maintenance: Boolean,
    val maintenanceProtocolMessage: String,
    val maintenanceMotd: Pair<String, String>,
    val motd: Pair<String, String>
)