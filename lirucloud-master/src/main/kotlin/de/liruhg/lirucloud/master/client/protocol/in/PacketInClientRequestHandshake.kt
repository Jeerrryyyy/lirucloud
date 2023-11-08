package de.liruhg.lirucloud.master.client.protocol.`in`

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.client.protocol.out.PacketOutClientHandshakeResult
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.network.NetworkConnectionRegistry
import de.liruhg.lirucloud.master.runtime.RuntimeVars
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInClientRequestHandshake : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInClientRequestHandshake::class.java)

    private val runtimeVars: RuntimeVars by LiruCloudMaster.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudMaster.KODEIN.instance()
    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()
    private val proxyGroupHandler: ProxyGroupHandler by LiruCloudMaster.KODEIN.instance()
    private val serverGroupHandler: ServerGroupHandler by LiruCloudMaster.KODEIN.instance()

    private lateinit var clientKey: String
    private lateinit var clientInfoModel: ClientInfoModel

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        this.clientInfoModel.channel = channelHandlerContext.channel()
        val clientName = "${this.clientInfoModel.name}${this.clientInfoModel.delimiter}${this.clientInfoModel.suffix}"

        if (!this.isClientAuthenticated(this.clientInfoModel.channel!!, clientName)) {
            this.networkUtil.sendPacket(
                PacketOutClientHandshakeResult(
                    "You are not authenticated. Please check your client key or the master configuration!",
                    false
                ), channelHandlerContext.channel()
            )
            this.logger.warn(
                "Blocked connection of unauthenticated Client with Name: [$clientName] - Remote: [${
                    channelHandlerContext.channel().remoteAddress().toString().replace("/", "")
                }]"
            )

            channelHandlerContext.channel().close()
            return
        }

        if (this.clientInfoModel.responsibleGroups.isEmpty()) {
            val proxyGroups = this.proxyGroupHandler.getGroups()
                .map { it.name }
                .toSet()

            val serverGroups = this.serverGroupHandler.getGroups()
                .map { it.name }
                .toSet()

            this.clientInfoModel.responsibleGroups = proxyGroups + serverGroups

            println(this.clientInfoModel.responsibleGroups)

            this.logger.warn("Client with Name: [$clientName] has no responsible groups assigned. Assigning to all groups!")
        }

        if (!this.clientRegistry.registerClient(this.clientInfoModel)) {
            this.networkUtil.sendPacket(
                PacketOutClientHandshakeResult(
                    "Failed to register client. Your uuid is already registered!",
                    false
                ), channelHandlerContext.channel()
            )
            this.logger.warn(
                "Failed to register Client with Name: [$clientName] - Remote: [${
                    channelHandlerContext.channel().remoteAddress().toString().replace("/", "")
                }]"
            )
            return
        }

        this.networkUtil.sendPacket(
            PacketOutClientHandshakeResult(
                "Handshake completed, connection with master established!",
                true
            ), channelHandlerContext.channel()
        )

        this.networkConnectionRegistry.unregisterDanglingConnection(channelHandlerContext.channel().id())

        this.logger.info(
            "Successfully registered Client with Name: [$clientName] - Remote: [${
                channelHandlerContext.channel().remoteAddress().toString().replace("/", "")
            }]"
        )
    }

    private fun isClientAuthenticated(channel: Channel, clientName: String): Boolean {
        if (this.runtimeVars.clientKey != this.clientKey) {
            return false
        }

        var returnValue = false
        val validClients = this.runtimeVars.cloudConfiguration.validClients

        validClients.forEach {
            val validClientName = it.clientName
            val validClientWhitelistedIps = it.whitelistedIps

            if (validClientName == clientName) {
                val clientIpAddress = channel.remoteAddress().toString().replace("/", "").split(":")[0]

                if (validClientWhitelistedIps.contains(clientIpAddress)) {
                    returnValue = true
                }
            }
        }

        return returnValue
    }
}