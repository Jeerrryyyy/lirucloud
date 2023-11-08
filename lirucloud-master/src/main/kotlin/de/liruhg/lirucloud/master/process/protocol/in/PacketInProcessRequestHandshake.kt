package de.liruhg.lirucloud.master.process.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.network.NetworkConnectionRegistry
import de.liruhg.lirucloud.master.process.registry.ProcessRegistry
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutProcessHandshakeResult
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutProcessUpdateStatus
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutProxyRegisterServer
import de.liruhg.lirucloud.master.runtime.RuntimeVars
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInProcessRequestHandshake : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInProcessRequestHandshake::class.java)

    private val processRegistry: ProcessRegistry by LiruCloudMaster.KODEIN.instance()
    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudMaster.KODEIN.instance()
    private val runtimeVars: RuntimeVars by LiruCloudMaster.KODEIN.instance()
    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()

    private lateinit var uuid: String
    private lateinit var clientKey: String

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()

        if (this.clientKey != this.runtimeVars.clientKey) {
            this.logger.warn("Received handshake with wrong client key of process with UUID: [${this.uuid}]")

            this.networkUtil.sendPacket(
                PacketOutProcessHandshakeResult("Wrong client key", false),
                channel
            )
            channel.close()
            return
        }

        this.logger.info("Received handshake of process with UUID: [${this.uuid}]")

        val process = this.processRegistry.getProcess(this.uuid)

        if (process != null) {
            this.handleProcess(process, channel)
            return
        }

        this.logger.warn("Received handshake for unknown process with UUID: [${this.uuid}]")

        this.networkUtil.sendPacket(
            PacketOutProcessHandshakeResult("Process was never requested", false),
            channel
        )

        channel.close()
    }

    private fun handleProcess(process: CloudProcess, channel: Channel) {
        process.channel = channel

        this.networkConnectionRegistry.unregisterDanglingConnection(channel.id())

        when (process.type) {
            ProcessType.PROXY -> {
                process.stage = ProcessStage.RUNNING

                val clientInfoModel = this.clientRegistry.getClient(process)

                if (clientInfoModel == null) {
                    this.logger.warn("Received handshake for unknown client of process with UUID: [${this.uuid}]")

                    channel.close()
                    return
                }

                this.networkUtil.sendPacket(
                    PacketOutProcessUpdateStatus(process.uuid!!, process.stage),
                    clientInfoModel.channel!!
                )
            }

            ProcessType.SERVER -> {
                this.processRegistry.processes.values.filter { it.type == ProcessType.PROXY }.forEach { proxyProcess ->
                    this.networkUtil.sendPacket(
                        PacketOutProxyRegisterServer(
                            process.uuid!!,
                            process.name!!,
                            process.mode,
                            process.ip,
                            process.port
                        ),
                        proxyProcess.channel!!
                    )
                }
            }
        }


        this.networkUtil.sendPacket(
            PacketOutProcessHandshakeResult("Successfully registered process", true),
            channel
        )

        this.logger.info("Successfully registered process with UUID: [${process.uuid}] - Name [${process.name}]")
    }
}