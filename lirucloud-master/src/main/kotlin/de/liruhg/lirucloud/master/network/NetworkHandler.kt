package de.liruhg.lirucloud.master.network

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.model.ProxyProcess
import de.liruhg.lirucloud.library.process.model.ServerProcess
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.process.ProcessRegistry
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val proxyProcessRegistry: ProcessRegistry<ProxyProcess> by LiruCloudMaster.KODEIN.instance()
    private val serverProcessRegistry: ProcessRegistry<ServerProcess> by LiruCloudMaster.KODEIN.instance()
    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.info(
            "New channel with Id: [${
                channelHandlerContext.channel().id()
            }] connected. Awaiting handshake."
        )
        this.networkConnectionRegistry.registerDanglingConnection(channelHandlerContext.channel())
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()
        var clientInfoModel = this.clientRegistry.getClientByChannel(channelHandlerContext.channel())

        if (clientInfoModel != null) {
            val clientName = "${clientInfoModel.name}${clientInfoModel.delimiter}${clientInfoModel.suffix}"

            this.clientRegistry.unregisterClient(clientInfoModel)

            this.logger.info(
                "Channel with Id: [${
                    channel.id()
                }] disconnected. Removing client with Id: [${clientInfoModel.uuid}] - Name: [$clientName]"
            )
        }

        val proxyProcess = this.proxyProcessRegistry.getProcessByChannel(channel)

        if (proxyProcess != null) {
            clientInfoModel = this.clientRegistry.getClient(proxyProcess)
            clientInfoModel?.runningProcesses?.remove(proxyProcess.uuid)

            this.proxyProcessRegistry.unregisterProcess(proxyProcess)

            this.logger.info(
                "Channel with Id: [${
                    channel.id()
                }] disconnected. Removing proxy process with Id: [${proxyProcess.uuid}] - Name: [${proxyProcess.name}]"
            )
        }

        val serverProcess = this.serverProcessRegistry.getProcessByChannel(channel)

        if (serverProcess != null) {
            clientInfoModel = this.clientRegistry.getClient(serverProcess)
            clientInfoModel?.runningProcesses?.remove(serverProcess.uuid)

            this.serverProcessRegistry.unregisterProcess(serverProcess)

            this.logger.info(
                "Channel with Id: [${
                    channel.id()
                }] disconnected. Removing server process with Id: [${serverProcess.uuid}] - Name: [${serverProcess.name}]"
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        this.logger.error(
            "Exception caught in channel with Id: [${
                channelHandlerContext.channel().id()
            }] - Reason: [${cause.message}]"
        )

        cause.printStackTrace()
    }
}