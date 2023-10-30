package de.liruhg.lirucloud.master.configuration.model

import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel

data class CloudConfigurationModel(
    val masterServerPort: Int,
    val masterWebPort: Int,
    val database: DatabaseConnectionModel,
    val validClients: Set<ValidClientModel>
)