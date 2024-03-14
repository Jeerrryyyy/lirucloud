package de.liruhg.lirucloud.master.group.proxy

import de.liruhg.lirucloud.library.proxy.ProxyInformation
import de.liruhg.lirucloud.master.group.Group

class ProxyGroup(
    name: String,
    minServersOnline: Int,
    maxMemory: Int,
    minMemory: Int,
    maxPlayers: Int,
    val proxyInformation: ProxyInformation
) : Group(
    name,
    minServersOnline,
    maxMemory,
    minMemory,
    maxPlayers
)