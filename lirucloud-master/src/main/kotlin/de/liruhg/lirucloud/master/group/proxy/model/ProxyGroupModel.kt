package de.liruhg.lirucloud.master.group.proxy.model

import de.liruhg.lirucloud.library.proxy.ProxyInformationModel
import de.liruhg.lirucloud.master.group.AbstractGroup

class ProxyGroupModel(
    name: String,
    minServersOnline: Int,
    maxMemory: Int,
    minMemory: Int,
    maxPlayers: Int,
    val proxyInformation: ProxyInformationModel
) : AbstractGroup(
    name,
    minServersOnline,
    maxMemory,
    minMemory,
    maxPlayers
)