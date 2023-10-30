package de.liruhg.lirucloud.master.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import java.nio.file.Path

class DefaultFolderCreator : Configuration {

    private val requiredFolders: Set<Path> = setOf(
        Path.of(Directories.MASTER_ROOT),
        Path.of(Directories.MASTER_CONFIGURATION),
        Path.of(Directories.MASTER_KEYS),
        Path.of(Directories.MASTER_SOFTWARE),
        Path.of(Directories.MASTER_SOFTWARE_PROXY),
        Path.of(Directories.MASTER_SOFTWARE_SERVER),
        Path.of(Directories.MASTER_SOFTWARE_PROXY_PLUGINS),
        Path.of(Directories.MASTER_SOFTWARE_SERVER_PLUGINS),
        Path.of(Directories.MASTER_GROUPS),
        Path.of(Directories.MASTER_GROUPS_PROXY),
        Path.of(Directories.MASTER_GROUPS_SERVER),
        Path.of(Directories.MASTER_TEMPLATE),
        Path.of(Directories.MASTER_TEMPLATE_PROXY),
        Path.of(Directories.MASTER_TEMPLATE_PROXY_TEMP),
        Path.of(Directories.MASTER_TEMPLATE_SERVER),
        Path.of(Directories.MASTER_TEMPLATE_SERVER_TEMP),
    )

    override fun execute() {
        for (requiredFolder in this.requiredFolders) {
            if (requiredFolder.toFile().exists()) continue

            FileUtils.createDirectory(requiredFolder)
        }
    }
}