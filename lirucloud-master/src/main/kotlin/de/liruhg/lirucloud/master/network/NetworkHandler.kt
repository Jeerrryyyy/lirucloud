package de.liruhg.lirucloud.master.network

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()
    //private val portUtil: PortUtil by LiruCloudMaster.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.info(
            "New channel with Id: [${
                channelHandlerContext.channel().id()
            }] connected. Awaiting handshake."
        )
        this.networkConnectionRegistry.registerDanglingConnection(
            channelHandlerContext.channel().id(),
            channelHandlerContext.channel()
        )
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()
        val clientInfo = this.clientRegistry.getClientByChannel(channelHandlerContext.channel())

        if (clientInfo != null) {
            val clientName = "${clientInfo.name}${clientInfo.delimiter}${clientInfo.suffix}"

            this.clientRegistry.unregisterClient(clientInfo)

            this.logger.info(
                "Channel with Id: [${
                    channel.id()
                }] disconnected. Removing client with Id: [${clientInfo.uuid}] - Name: [$clientName]"
            )
        }

        //val process = this.processRegistry.getProcessByChannel(channel)

        //if (process != null) {
        //    clientInfoModel = this.clientRegistry.getClient(process)
        //    clientInfoModel?.runningProcesses?.remove(process.uuid)

        //    this.processRegistry.removeProcess(process)
        //    this.processRegistry.removeChannel(process.uuid!!)
        //    this.portUtil.unblockPort(process.port)

        //    this.logger.info(
        //        "Channel with Id: [${
        //            channel.id()
        //        }] disconnected. Removing process with Id: [${process.uuid}] - Name: [${process.name}]"
        //    )
        //}
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        this.logger.error(
            "Exception caught in channel with Id: [${
                channelHandlerContext.channel().id()
            }] - Reason: [${cause.message}]"
        )
    }
}