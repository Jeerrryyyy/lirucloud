package de.liruhg.lirucloud.master.process.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutProcessUpdateStatus
import de.liruhg.lirucloud.master.process.registry.ProcessRegistry
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInProxyRegisteredServer : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInProxyRegisteredServer::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val processRegistry: ProcessRegistry by LiruCloudMaster.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudMaster.KODEIN.instance()

    private lateinit var uuid: String

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val process = this.processRegistry.getProcess(this.uuid)

        if (process == null || process.stage == ProcessStage.RUNNING) {
            return
        }

        process.stage = ProcessStage.RUNNING

        val clientInfoModel = this.clientRegistry.getClient(process)

        if (clientInfoModel == null) {
            this.logger.warn("Received registered process for unknown client of process with UUID: [${this.uuid}]")
            return
        }

        this.networkUtil.sendPacket(
            PacketOutProcessUpdateStatus(process.uuid!!, process.stage),
            clientInfoModel.channel!!
        )
    }
}