package de.liruhg.lirucloud.master.configuration.model

import de.liruhg.lirucloud.library.configuration.model.CacheConnectionModel
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel

data class CloudConfigurationModel(
    val masterServerPort: Int,
    val masterWebPort: Int,
    val database: DatabaseConnectionModel,
    val cache: CacheConnectionModel,
    val validClients: Set<ValidClientModel>
)