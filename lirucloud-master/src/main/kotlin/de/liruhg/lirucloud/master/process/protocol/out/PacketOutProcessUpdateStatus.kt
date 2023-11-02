package de.liruhg.lirucloud.master.process.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.ProcessStage

class PacketOutProcessUpdateStatus(
    val uuid: String,
    val stage: ProcessStage
) : Packet