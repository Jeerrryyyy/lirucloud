package de.liruhg.lirucloud.library.client

import io.netty.channel.Channel

data class ClientInfo(
    val uuid: String,
    val name: String,
    val delimiter: String,
    val suffix: String,
    var currentOnlineServers: Int,
    val memory: Long,
    var currentMemoryUsage: Long,
    var currentCpuUsage: Double,
    var responsibleGroups: Set<String>,
    var channel: Channel? = null,
    var runningProcesses: MutableSet<String> = mutableSetOf()
)