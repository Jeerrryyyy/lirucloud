package de.liruhg.lirucloud.master.command

import de.liruhg.lirucloud.library.command.Command
import de.liruhg.lirucloud.library.command.CommandInformation
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.proxy.model.ProxyGroupModel
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.group.server.model.ServerGroupModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

@CommandInformation(
    command = "update",
    description = "This command will update the group files in the bucket! Usage: update <group> (if no group is specified, all groups will be updated)",
    aliases = ["u", "updategroupfiles", "updatefiles", "updategroup", "updategroups"]
)
class UpdateGroupFiles(
    private val proxyGroupHandler: ProxyGroupHandler,
    private val serverGroupHandler: ServerGroupHandler,
    private val fileHandler: SyncFileHandler
) : Command {

    private val logger: Logger = LoggerFactory.getLogger(UpdateGroupFiles::class.java)

    override fun execute(args: Array<String>): Boolean {
        when (args.size) {
            1 -> {
                this.proxyGroupHandler.groups.forEach { (_, group) ->
                    this.updateProxyGroup(group)
                }

                this.serverGroupHandler.groups.forEach { (_, group) ->
                    this.updateServerGroup(group)
                }
            }

            2 -> {
                val group = args[1]

                if (this.proxyGroupHandler.groups.containsKey(group)) {
                    this.updateProxyGroup(this.proxyGroupHandler.groups[group]!!)
                    return true
                }

                if (this.serverGroupHandler.groups.containsKey(group)) {
                    this.updateServerGroup(this.serverGroupHandler.groups[group]!!)
                    return true
                }

                this.logger.error("Could not find group with Name: [$group]")
            }

            else -> {
                this.logger.error("Invalid arguments! Usage: update <group> (if no group is specified, all groups will be updated)")
            }
        }

        return true
    }

    private fun updateProxyGroup(proxyGroupModel: ProxyGroupModel) {
        this.logger.info("Updating group with Name: [${proxyGroupModel.name}]")

        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_PROXY}/${proxyGroupModel.name}")
        val defaultTemplatePath = Path.of("${Directories.MASTER_TEMPLATE_PROXY}/${proxyGroupModel.name}/default")

        FileUtils.deleteIfExists(Path.of("${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${proxyGroupModel.name}.zip"))

        FileUtils.copyAllFiles(
            File(Directories.MASTER_SOFTWARE_PROXY_PLUGINS).toPath(),
            File(defaultTemplatePath.toFile(), "plugins").path
        )

        FileUtils.copyAllFiles(
            templatePath,
            "${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${proxyGroupModel.name}"
        )
        FileUtils.zipDirectory(
            "${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${proxyGroupModel.name}",
            File("${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${proxyGroupModel.name}.zip")
        )

        FileUtils.deleteFullDirectory(File("${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${proxyGroupModel.name}"))

        val hashedName = HashUtils.hashStringMD5(proxyGroupModel.name)
        this.fileHandler.uploadFile(
            File(Directories.MASTER_TEMPLATE_PROXY_TEMP, "${proxyGroupModel.name}.zip"),
            hashedName
        )

        this.logger.info("Successfully updated group with Name: [${proxyGroupModel.name}]")
    }

    private fun updateServerGroup(serverGroupModel: ServerGroupModel) {
        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${serverGroupModel.name}")
        val defaultTemplatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${serverGroupModel.name}/default")

        FileUtils.deleteIfExists(Path.of("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${serverGroupModel.name}.zip"))

        FileUtils.copyAllFiles(
            File(Directories.MASTER_SOFTWARE_SERVER_PLUGINS).toPath(),
            File(defaultTemplatePath.toFile(), "plugins").path
        )

        FileUtils.copyAllFiles(
            templatePath,
            "${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${serverGroupModel.name}"
        )
        FileUtils.zipDirectory(
            "${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${serverGroupModel.name}",
            File("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${serverGroupModel.name}.zip")
        )

        FileUtils.deleteFullDirectory(File("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${serverGroupModel.name}"))
        val hashedName = HashUtils.hashStringMD5(serverGroupModel.name)
        this.fileHandler.uploadFile(
            File(Directories.MASTER_TEMPLATE_SERVER_TEMP, "${serverGroupModel.name}.zip"),
            hashedName
        )

        this.logger.info("Successfully updated group with Name: [${serverGroupModel.name}]")
    }
}