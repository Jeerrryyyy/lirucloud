package de.liruhg.lirucloud.master.command

import de.liruhg.lirucloud.library.command.Command
import de.liruhg.lirucloud.library.command.CommandInformation
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.proxy.model.ProxyGroupModel
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.group.server.model.ServerGroupModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

@CommandInformation(
    command = "listgroups",
    description = "This command will list all groups with their information! Usage: list <group> (if no group is specified, all groups will be listed)",
    aliases = ["lg", "listgroup"]
)
class ListGroupsCommand(
    private val proxyGroupHandler: ProxyGroupHandler,
    private val serverGroupHandler: ServerGroupHandler
) : Command {

    private val logger: Logger = LoggerFactory.getLogger(ListGroupsCommand::class.java)

    override fun execute(args: Array<String>): Boolean {
        when (args.size) {
            1 -> {
                this.logger.info("Proxy Groups:")
                this.proxyGroupHandler.getGroups().forEach {
                    this.printProxyGroup(it)
                }

                this.logger.info("Server Groups:")
                this.serverGroupHandler.getGroups().forEach {
                    this.printServerGroup(it)
                }
            }

            2 -> {
                val group = args[1]

                if (this.proxyGroupHandler.groupExists(group)) {
                    this.printProxyGroup(this.proxyGroupHandler.getGroup(group)!!)
                    return true
                }

                if (this.serverGroupHandler.groupExists(group)) {
                    this.printServerGroup(this.serverGroupHandler.getGroup(group)!!)
                    return true
                }

                this.logger.error("Could not find group with Name: [$group]")
            }

            else -> {
                this.logger.error("Invalid arguments! Usage: list <group> (if no group is specified, all groups will be listed)")
            }
        }
        return true
    }

    private fun printProxyGroup(proxyGroupModel: ProxyGroupModel) {
        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_PROXY}/${proxyGroupModel.name}")

        this.logger.info("Group Name: [${proxyGroupModel.name}] - MinServersOnline: [${proxyGroupModel.minServersOnline}] - MaxMemory: [${proxyGroupModel.maxMemory}] - MinMemory: [${proxyGroupModel.minMemory}] - MaxPlayers: [${proxyGroupModel.maxPlayers}] - JoinPower: [${proxyGroupModel.proxyInformation.joinPower}] - Maintenance: [${proxyGroupModel.proxyInformation.maintenance}] - Path: [${templatePath.toAbsolutePath()}]")
    }

    private fun printServerGroup(serverGroupModel: ServerGroupModel) {
        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${serverGroupModel.name}")

        this.logger.info(
            "Group Name: [${serverGroupModel.name}] - MinServersOnline: [${serverGroupModel.minServersOnline}] - MaxMemory: [${serverGroupModel.maxMemory}] - MinMemory: [${serverGroupModel.minMemory}] - MaxPlayers: [${serverGroupModel.maxPlayers}] - JoinPower: [${serverGroupModel.serverInformation.joinPower}] - Maintenance: [${serverGroupModel.serverInformation.maintenance}] - Template: [${serverGroupModel.template}] - NewServerPercentage: [${serverGroupModel.newServerPercentage}] - ServerMode: [${serverGroupModel.mode}] - RandomTemplateMode: [${serverGroupModel.randomTemplateMode}] - TemplateModes: [${
                serverGroupModel.templateModes.joinToString(
                    ", "
                )
            }] - Path: [${templatePath.toAbsolutePath()}]"
        )
    }
}