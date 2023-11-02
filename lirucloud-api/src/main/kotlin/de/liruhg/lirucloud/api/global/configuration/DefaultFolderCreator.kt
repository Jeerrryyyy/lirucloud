package de.liruhg.lirucloud.api.global.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.util.FileUtils
import java.nio.file.Path

class DefaultFolderCreator(
    private val requiredFolders: Set<Path>
): Configuration {

    override fun execute() {
        for (requiredFolder in this.requiredFolders) {
            if (requiredFolder.toFile().exists()) continue

            FileUtils.createDirectory(requiredFolder)
        }
    }
}