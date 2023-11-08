package de.liruhg.lirucloud.master.process.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.CloudProcess

data class PacketOutRequestProcess(
    private val cloudProcess: CloudProcess
) : Packet