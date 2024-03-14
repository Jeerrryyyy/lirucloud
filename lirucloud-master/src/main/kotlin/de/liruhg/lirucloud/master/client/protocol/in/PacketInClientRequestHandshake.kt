package de.liruhg.lirucloud.master.client.protocol.`in`

import de.liruhg.lirucloud.library.client.ClientInfo
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.client.protocol.out.PacketOutClientHandshakeResult
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.network.NetworkConnectionRegistry
import de.liruhg.lirucloud.master.process.proxy.ProxyProcessRequestHandler
import de.liruhg.lirucloud.master.process.server.ServerProcessRequestHandler
import de.liruhg.lirucloud.master.store.Store
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInClientRequestHandshake : Packet() {

    private val logger: Logger = LoggerFactory.getLogger(PacketInClientRequestHandshake::class.java)

    private val store: Store by LiruCloudMaster.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudMaster.KODEIN.instance()
    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()
    private val proxyGroupHandler: ProxyGroupHandler by LiruCloudMaster.KODEIN.instance()
    private val serverGroupHandler: ServerGroupHandler by LiruCloudMaster.KODEIN.instance()
    private val proxyProcessRequestHandler: ProxyProcessRequestHandler by LiruCloudMaster.KODEIN.instance()
    private val serverProcessRequestHandler: ServerProcessRequestHandler by LiruCloudMaster.KODEIN.instance()

    private lateinit var clientKey: String
    private lateinit var clientInfo: ClientInfo

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()

        this.clientInfo.channel = channel

        val clientName = "${this.clientInfo.name}${this.clientInfo.delimiter}${this.clientInfo.suffix}"

        if (!this.isClientAuthenticated(this.clientInfo.channel!!, clientName)) {
            this.networkUtil.sendResponse(
                this,
                PacketOutClientHandshakeResult(
                    "You are not authenticated. Please check your client key or the master configuration!",
                    false
                ),
                channel
            )
            this.logger.warn(
                "Blocked connection of unauthenticated Client with Name: [$clientName] - Remote: [${
                    channel.remoteAddress().toString().replace("/", "")
                }]"
            )

            channel.close()
            return
        }

        if (this.clientInfo.responsibleGroups.isEmpty()) {
            val proxyGroups = this.proxyGroupHandler.getGroups()
                .map { it.name }
                .toSet()

            val serverGroups = this.serverGroupHandler.getGroups()
                .map { it.name }
                .toSet()

            this.clientInfo.responsibleGroups = proxyGroups + serverGroups

            this.logger.warn("Client with Name: [$clientName] has no responsible groups assigned. Assigning to all groups!")
        }

        if (!this.clientRegistry.registerClient(this.clientInfo)) {
            this.networkUtil.sendResponse(
                this,
                PacketOutClientHandshakeResult(
                    "Failed to register client. Your uuid is already registered!",
                    false
                ), channel
            )
            this.logger.warn(
                "Failed to register Client with Name: [$clientName] - Remote: [${
                    channel.remoteAddress().toString().replace("/", "")
                }]"
            )
            return
        }

        this.networkUtil.sendResponse(
            this,
            PacketOutClientHandshakeResult(
                "Handshake completed, connection with master established!",
                true
            ), channel
        )

        this.networkConnectionRegistry.unregisterDanglingConnection(channel.id())

        this.logger.info(
            "Successfully registered Client with Name: [$clientName] - Remote: [${
                channel.remoteAddress().toString().replace("/", "")
            }]"
        )

        if (this.clientInfo.responsibleGroups.contains(this.store.cloudConfiguration.defaultProxyGroupName)) {
            this.proxyProcessRequestHandler.checkDefaultProcesses(this.clientInfo)
        }

        if (this.clientInfo.responsibleGroups.contains(this.store.cloudConfiguration.defaultLobbyGroupName)) {
            this.serverProcessRequestHandler.checkDefaultProcesses(this.clientInfo)
        }
    }

    private fun isClientAuthenticated(channel: Channel, clientName: String): Boolean {
        if (this.store.clientKey != this.clientKey) {
            return false
        }

        var returnValue = false
        val validClients = this.store.cloudConfiguration.validClients

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