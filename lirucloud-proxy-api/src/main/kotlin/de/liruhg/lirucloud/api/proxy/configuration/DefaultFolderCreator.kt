package de.liruhg.lirucloud.api.proxy.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import java.nio.file.Path

class DefaultFolderCreator : Configuration {

    private val requiredFolders: Set<Path> = setOf(
        Path.of(Directories.PROXY_PLUGINS),
        Path.of(Directories.PROXY_PLUGINS_API),
    )

    override fun execute() {
        for (requiredFolder in this.requiredFolders) {
            if (requiredFolder.toFile().exists()) continue

            FileUtils.createDirectory(requiredFolder)
        }
    }
}