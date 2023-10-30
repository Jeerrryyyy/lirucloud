package de.liruhg.lirucloud.master.group.proxy.model

import de.liruhg.lirucloud.master.group.AbstractGroup

open class ProxyGroupModel(
    name: String,
    maxServersOnline: Int,
    minServersOnline: Int,
    maxMemory: Int,
    minMemory: Int,
    maxPlayers: Int,
    joinPower: Int,
    maintenance: Boolean,
): AbstractGroup(name, maxServersOnline, minServersOnline, maxMemory, minMemory, maxPlayers, joinPower, maintenance)