package de.liruhg.lirucloud.client.configuration.model

import de.liruhg.lirucloud.library.configuration.model.CacheConnection
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnection

data class CloudConfiguration(
    val masterAddress: String,
    val masterPort: Int,
    val name: String,
    val delimiter: String,
    val suffix: String,
    val memory: Long,
    val uuid: String,
    val responsibleGroups: Set<String>,
    val database: DatabaseConnection,
    val cache: CacheConnection,
)