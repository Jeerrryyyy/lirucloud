package de.liruhg.lirucloud.master.group.proxy.model

import de.liruhg.lirucloud.master.group.AbstractGroup

class ProxyGroupModel(
    name: String,
    maxServersOnline: Int,
    minServersOnline: Int,
    maxMemory: Int,
    minMemory: Int,
    maxPlayers: Int,
    joinPower: Int,
    maintenance: Boolean,
    maintenanceProtocolMessage: String,
    maintenanceMotd: Pair<String, String>,
    motd: Pair<String, String>
) : AbstractGroup(
    name,
    maxServersOnline,
    minServersOnline,
    maxMemory,
    minMemory,
    maxPlayers,
    joinPower,
    maintenance,
    maintenanceProtocolMessage,
    maintenanceMotd,
    motd
)