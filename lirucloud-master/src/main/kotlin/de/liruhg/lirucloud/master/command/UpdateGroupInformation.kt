package de.liruhg.lirucloud.master.command

import de.liruhg.lirucloud.library.command.Command
import de.liruhg.lirucloud.library.command.CommandInformation
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.process.proxy.protocol.out.PacketOutUpdateProxyInformation
import de.liruhg.lirucloud.master.process.registry.ProcessRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CommandInformation(
    command = "updateinfo",
    description = "This command will update the group information and send it to running servers! Usage: updateinfo <group> (if no group is specified, all groups will be updated)",
    aliases = ["updategroupinfo"]
)
class UpdateGroupInformation(
    private val proxyGroupHandler: ProxyGroupHandler,
    private val serverGroupHandler: ServerGroupHandler,
    private val networkUtil: NetworkUtil,
    private val processRegistry: ProcessRegistry
) : Command {

    private val logger: Logger = LoggerFactory.getLogger(UpdateGroupInformation::class.java)

    override fun execute(args: Array<String>): Boolean {
        when (args.size) {
            1 -> {
                this.proxyGroupHandler.updateGroups()

                this.proxyGroupHandler.getGroups().forEach {
                    this.logger.info("Updating group with Name: [${it.name}]")

                    for (cloudProcess in this.processRegistry.getProcesses(it.name)) {
                        val channel = this.processRegistry.getChannel(cloudProcess.uuid!!) ?: continue

                        this.networkUtil.sendPacket(PacketOutUpdateProxyInformation(it.proxyInformation), channel)
                    }

                    this.logger.info("Successfully updated group with Name: [${it.name}]")
                }

                this.serverGroupHandler.getGroups().forEach {
                    // TODO
                }
            }

            2 -> {
                val group = args[1]

                if (this.proxyGroupHandler.groupExists(group)) {
                    this.logger.info("Updating group with Name: [$group]")
                    this.proxyGroupHandler.updateGroup(group)

                    val proxyGroup = this.proxyGroupHandler.getGroup(group)!!

                    for (cloudProcess in this.processRegistry.getProcesses(proxyGroup.name)) {
                        val channel = this.processRegistry.getChannel(cloudProcess.uuid!!) ?: continue

                        this.networkUtil.sendPacket(
                            PacketOutUpdateProxyInformation(proxyGroup.proxyInformation),
                            channel
                        )
                    }
                    this.logger.info("Successfully updated group with Name: [$group]")
                    return true
                }

                if (this.serverGroupHandler.groupExists(group)) {
                    // TODO
                    return true
                }

                this.logger.error("Could not find group with Name: [$group]")
            }

            else -> {
                this.logger.error("Invalid arguments! Usage: updateinfo <group> (if no group is specified, all groups will be updated)")
            }
        }

        return true
    }
}