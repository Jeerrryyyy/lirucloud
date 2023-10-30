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
                this.proxyGroupHandler.groups.forEach { (_, group) ->
                    this.printProxyGroup(group)
                }

                this.logger.info("Server Groups:")
                this.serverGroupHandler.groups.forEach { (_, group) ->
                    this.printServerGroup(group)
                }
            }

            2 -> {
                val group = args[1]

                if (this.proxyGroupHandler.groups.containsKey(group)) {
                    this.printProxyGroup(this.proxyGroupHandler.groups[group]!!)
                    return true
                }

                if (this.serverGroupHandler.groups.containsKey(group)) {
                    this.printServerGroup(this.serverGroupHandler.groups[group]!!)
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

        this.logger.info("Group Name: [${proxyGroupModel.name}] - MaxServersOnline: [${proxyGroupModel.maxServersOnline}] - MinServersOnline: [${proxyGroupModel.minServersOnline}] - MaxMemory: [${proxyGroupModel.maxMemory}] - MinMemory: [${proxyGroupModel.minMemory}] - MaxPlayers: [${proxyGroupModel.maxPlayers}] - JoinPower: [${proxyGroupModel.joinPower}] - Maintenance: [${proxyGroupModel.maintenance}] - Path: [${templatePath.toAbsolutePath()}]")
    }

    private fun printServerGroup(serverGroupModel: ServerGroupModel) {
        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${serverGroupModel.name}")

        this.logger.info(
            "Group Name: [${serverGroupModel.name}] - MaxServersOnline: [${serverGroupModel.maxServersOnline}] - MinServersOnline: [${serverGroupModel.minServersOnline}] - MaxMemory: [${serverGroupModel.maxMemory}] - MinMemory: [${serverGroupModel.minMemory}] - MaxPlayers: [${serverGroupModel.maxPlayers}] - JoinPower: [${serverGroupModel.joinPower}] - Maintenance: [${serverGroupModel.maintenance}] - Template: [${serverGroupModel.template}] - NewServerPercentage: [${serverGroupModel.newServerPercentage}] - ServerMode: [${serverGroupModel.mode}] - RandomTemplateMode: [${serverGroupModel.randomTemplateMode}] - TemplateModes: [${
                serverGroupModel.templateModes.joinToString(
                    ", "
                )
            }] - Path: [${templatePath.toAbsolutePath()}]"
        )
    }
}