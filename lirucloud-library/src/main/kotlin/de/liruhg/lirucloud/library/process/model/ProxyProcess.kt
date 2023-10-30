package de.liruhg.lirucloud.library.process.model

import de.liruhg.lirucloud.library.process.AbstractProcess
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType

open class ProxyProcess(
    groupName: String,
    name: String?,
    uuid: String?,
    ip: String,
    type: ProcessType,
    stage: ProcessStage,
    minMemory: Int,
    maxMemory: Int,
    port: Int,
    maxPlayers: Int,
    joinPower: Int,
    maintenance: Boolean,
) : AbstractProcess(
    groupName,
    name,
    uuid,
    ip,
    type,
    stage,
    minMemory,
    maxMemory,
    port,
    maxPlayers,
    joinPower,
    maintenance
)