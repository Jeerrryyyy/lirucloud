package de.liruhg.lirucloud.master.group.server.model

import de.liruhg.lirucloud.library.process.ProcessMode
import de.liruhg.lirucloud.library.server.ServerInformationModel
import de.liruhg.lirucloud.master.group.AbstractGroup

class ServerGroupModel(
    name: String,
    minServersOnline: Int,
    maxMemory: Int,
    minMemory: Int,
    maxPlayers: Int,
    val serverInformation: ServerInformationModel,
    val template: String,
    var newServerPercentage: Int,
    var mode: ProcessMode,
    var randomTemplateMode: Boolean,
    var templateModes: MutableSet<String>,
) : AbstractGroup(
    name,
    minServersOnline,
    maxMemory,
    minMemory,
    maxPlayers,
)