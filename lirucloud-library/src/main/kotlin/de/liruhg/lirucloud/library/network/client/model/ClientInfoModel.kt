package de.liruhg.lirucloud.library.network.client.model

import io.netty.channel.Channel

data class ClientInfoModel(
    val uuid: String,
    val name: String,
    val delimiter: String,
    val suffix: String,
    var currentOnlineServers: Int,
    val memory: Long,
    var currentMemoryUsage: Long,
    var currentCpuUsage: Double,
    var responsibleGroups: Set<String>,
    var channel: Channel? = null
)