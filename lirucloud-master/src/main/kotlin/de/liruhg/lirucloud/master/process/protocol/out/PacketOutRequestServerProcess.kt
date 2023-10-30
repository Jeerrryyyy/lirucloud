package de.liruhg.lirucloud.master.process.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.model.ServerProcess

data class PacketOutRequestServerProcess(
    private val serverProcess: ServerProcess
) : Packet