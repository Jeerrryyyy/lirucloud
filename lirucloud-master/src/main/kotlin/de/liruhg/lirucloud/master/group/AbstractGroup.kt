package de.liruhg.lirucloud.master.group

abstract class AbstractGroup(
    val name: String,
    val maxServersOnline: Int,
    val minServersOnline: Int,
    val maxMemory: Int,
    val minMemory: Int,
    val maxPlayers: Int,
    val joinPower: Int,
    val maintenance: Boolean,
    val maintenanceProtocolMessage: String,
    val maintenanceMotd: Pair<String, String>,
    val motd: Pair<String, String>
)