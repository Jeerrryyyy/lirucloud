package de.liruhg.lirucloud.master.group.server

import de.liruhg.lirucloud.library.process.ProcessMode
import de.liruhg.lirucloud.library.server.ServerInformation
import de.liruhg.lirucloud.master.group.Group

class ServerGroup(
    name: String,
    minServersOnline: Int,
    maxMemory: Int,
    minMemory: Int,
    maxPlayers: Int,
    val serverInformation: ServerInformation,
    val template: String,
    var newServerPercentage: Int,
    var mode: ProcessMode,
    var randomTemplateMode: Boolean,
    var templateModes: MutableSet<String>,
) : Group(
    name,
    minServersOnline,
    maxMemory,
    minMemory,
    maxPlayers,
)