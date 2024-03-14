package de.liruhg.lirucloud.library.process.model

import de.liruhg.lirucloud.library.configuration.model.CacheConnection
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnection

data class PluginConfiguration(
    val masterAddress: String,
    val masterPort: Int,
    val database: DatabaseConnection,
    val cache: CacheConnection,
    val processInformation: ProcessInformation
)