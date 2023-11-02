package de.liruhg.lirucloud.library.process

import io.netty.channel.Channel

abstract class AbstractProcess(
    val groupName: String,
    var name: String?,
    var uuid: String?,
    val ip: String,
    val type: ProcessType,
    var stage: ProcessStage,
    val minMemory: Int,
    val maxMemory: Int,
    var port: Int,
    val maxPlayers: Int,
    var channel: Channel? = null
)