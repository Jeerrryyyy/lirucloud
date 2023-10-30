package de.liruhg.lirucloud.master.group.server

import de.liruhg.lirucloud.library.database.handler.FileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import de.liruhg.lirucloud.master.group.GroupHandler
import de.liruhg.lirucloud.master.group.server.model.ServerGroupModel
import java.io.File
import java.nio.file.Path

class ServerGroupHandler(
    private val fileHandler: FileHandler
) : GroupHandler<ServerGroupModel>() {

    override fun createGroup(group: ServerGroupModel) {
        FileUtils.writeClassToJsonFile(File(Directories.MASTER_GROUPS_SERVER, "${group.name}.json"), group)

        this.groups[group.name] = group

        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${group.name}")
        val defaultTemplatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${group.name}/default")

        FileUtils.createDirectory(templatePath)
        FileUtils.createDirectory(defaultTemplatePath)
        FileUtils.createDirectory(File(defaultTemplatePath.toFile(), "plugins").toPath())

        FileUtils.copyAllFiles(
            File(Directories.MASTER_SOFTWARE_SERVER_PLUGINS).toPath(),
            File(defaultTemplatePath.toFile(), "plugins").path
        )

        FileUtils.writeStringToFile(
            File(defaultTemplatePath.toFile(), "LICENSE.txt"),
            "This server is provided by LiruCloud entirely written by JevzoTV. You are not allowed to redistribute this software or claim it as your own."
        )

        FileUtils.copyAllFiles(
            templatePath,
            "${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}"
        )
        FileUtils.zipDirectory(
            "${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}",
            File("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}.zip")
        )

        FileUtils.deleteFullDirectory(File("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}"))

        val hashedName = HashUtils.hashStringMD5(group.name)
        this.fileHandler.uploadFile(File(Directories.MASTER_TEMPLATE_SERVER_TEMP, "${group.name}.zip"), hashedName)

        this.logger.info("Successfully created group with Name: [${group.name}]")
    }

    override fun deleteGroup(group: ServerGroupModel) {
        val hashedName = HashUtils.hashStringMD5(group.name)

        this.groups.remove(group.name)
        this.fileHandler.deleteFile(hashedName)

        FileUtils.deleteFullDirectory(Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${group.name}"))
        FileUtils.deleteIfExists(Path.of("${Directories.MASTER_GROUPS_SERVER}/${group.name}.json"))

        this.logger.info("Successfully deleted group with Name: [${group.name}]")
    }
}