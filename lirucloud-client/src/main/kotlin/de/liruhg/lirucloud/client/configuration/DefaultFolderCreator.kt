package de.liruhg.lirucloud.client.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import java.nio.file.Path

class DefaultFolderCreator : Configuration {

    private val requiredFolders: Set<Path> = setOf(
        Path.of(Directories.CLIENT_ROOT),
        Path.of(Directories.CLIENT_CONFIGURATION),
        Path.of(Directories.CLIENT_KEYS),
        Path.of(Directories.CLIENT_STATIC),
        Path.of(Directories.CLIENT_STATIC_PROXY),
        Path.of(Directories.CLIENT_STATIC_SERVER),
        Path.of(Directories.CLIENT_CACHED_TEMPLATES),
        Path.of(Directories.CLIENT_CACHED_TEMPLATES_PROXY),
        Path.of(Directories.CLIENT_CACHED_TEMPLATES_SERVER),
        Path.of(Directories.CLIENT_RUNNING_PROCESSES),
        Path.of(Directories.CLIENT_RUNNING_PROCESSES_PROXY),
        Path.of(Directories.CLIENT_RUNNING_PROCESSES_SERVER),
    )

    override fun execute() {
        for (requiredFolder in this.requiredFolders) {
            if (requiredFolder.toFile().exists()) continue

            FileUtils.createDirectory(requiredFolder)
        }
    }
}