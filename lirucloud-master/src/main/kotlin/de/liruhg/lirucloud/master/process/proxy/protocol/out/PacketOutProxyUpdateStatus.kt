package de.liruhg.lirucloud.master.process.proxy.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.ProcessStage

class PacketOutProxyUpdateStatus(
    val uuid: String,
    val stage: ProcessStage
) : Packet