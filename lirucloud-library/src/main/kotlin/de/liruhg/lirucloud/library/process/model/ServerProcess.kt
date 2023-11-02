package de.liruhg.lirucloud.library.process.model

import de.liruhg.lirucloud.library.process.AbstractProcess
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.process.ServerMode
import io.netty.channel.Channel

open class ServerProcess(
    groupName: String,
    name: String? = null,
    uuid: String? = null,
    ip: String,
    type: ProcessType,
    stage: ProcessStage,
    minMemory: Int,
    maxMemory: Int,
    port: Int,
    maxPlayers: Int,
    channel: Channel? = null,
    val mode: ServerMode
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
    channel
)