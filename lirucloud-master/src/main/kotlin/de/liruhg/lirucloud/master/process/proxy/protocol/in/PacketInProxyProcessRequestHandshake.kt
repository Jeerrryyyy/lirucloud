package de.liruhg.lirucloud.master.process.proxy.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.model.ProxyProcess
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.network.NetworkConnectionRegistry
import de.liruhg.lirucloud.master.process.ProcessRegistry
import de.liruhg.lirucloud.master.process.proxy.protocol.out.PacketOutProxyHandshakeResult
import de.liruhg.lirucloud.master.process.proxy.protocol.out.PacketOutProxyUpdateStatus
import de.liruhg.lirucloud.master.runtime.RuntimeVars
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInProxyProcessRequestHandshake : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInProxyProcessRequestHandshake::class.java)

    private val proxyProcessRegistry: ProcessRegistry<ProxyProcess> by LiruCloudMaster.KODEIN.instance()
    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudMaster.KODEIN.instance()
    private val runtimeVars: RuntimeVars by LiruCloudMaster.KODEIN.instance()
    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()

    private lateinit var uuid: String
    private lateinit var clientKey: String

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()
        val proxyProcess = this.proxyProcessRegistry.getDanglingProcess(this.uuid)

        if (proxyProcess == null) {
            this.logger.warn("Received handshake for unknown process with UUID: [${this.uuid}]")

            this.networkUtil.sendPacket(
                PacketOutProxyHandshakeResult("Process was never requested", false),
                channel
            )
            channel.close()
            return
        }

        proxyProcess.channel = channel
        proxyProcess.stage = ProcessStage.RUNNING

        if (this.clientKey != this.runtimeVars.clientKey) {
            this.logger.warn("Received handshake with wrong client key of process with UUID: [${this.uuid}]")

            this.networkUtil.sendPacket(
                PacketOutProxyHandshakeResult("Wrong client key", false),
                channel
            )
            channel.close()
            return
        }

        this.networkConnectionRegistry.unregisterDanglingConnection(channel)
        this.proxyProcessRegistry.unregisterDanglingProcess(proxyProcess)
        this.proxyProcessRegistry.registerProcess(proxyProcess)

        this.networkUtil.sendPacket(
            PacketOutProxyHandshakeResult("Successfully registered process", true),
            channel
        )

        val clientInfoModel = this.clientRegistry.getClient(proxyProcess)

        if (clientInfoModel == null) {
            this.logger.warn("Received handshake for unknown client of process with UUID: [${this.uuid}]")

            channel.close()
            return
        }

        this.networkUtil.sendPacket(
            PacketOutProxyUpdateStatus(proxyProcess.uuid!!, proxyProcess.stage),
            clientInfoModel.channel!!
        )

        this.logger.info("Successfully registered process with UUID: [${this.uuid}] - Name [${proxyProcess.name}]")
    }
}