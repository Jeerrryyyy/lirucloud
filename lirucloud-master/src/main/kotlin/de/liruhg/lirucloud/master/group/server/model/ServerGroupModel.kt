package de.liruhg.lirucloud.master.group.server.model

import de.liruhg.lirucloud.library.process.ServerMode
import de.liruhg.lirucloud.master.group.AbstractGroup

open class ServerGroupModel(
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
    motd: Pair<String, String>,
    val template: String,
    var newServerPercentage: Int,
    var mode: ServerMode,
    var randomTemplateMode: Boolean,
    var templateModes: MutableSet<String>,
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