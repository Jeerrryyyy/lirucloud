package de.liruhg.lirucloud.master.configuration.model

import de.liruhg.lirucloud.library.configuration.model.CacheConnection
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnection

data class CloudConfiguration(
    val serverPort: Int,
    val database: DatabaseConnection,
    val cache: CacheConnection,
    val validClients: Set<ValidClient>
)