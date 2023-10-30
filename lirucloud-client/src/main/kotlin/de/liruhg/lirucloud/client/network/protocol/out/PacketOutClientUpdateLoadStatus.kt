package de.liruhg.lirucloud.client.network.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutClientUpdateLoadStatus(
    val currentOnlineServers: Int,
    val currentMemoryUsage: Long,
    val currentCpuUsage: Double
) : Packet