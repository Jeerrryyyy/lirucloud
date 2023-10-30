package de.liruhg.lirucloud.client.configuration.model

import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel

data class CloudConfigurationModel(
    val masterAddress: String,
    val masterPort: Int,
    val webPort: Int,
    val name: String,
    val delimiter: String,
    val suffix: String,
    val memory: Long,
    val uuid: String,
    val responsibleGroups: Set<String>,
    val database: DatabaseConnectionModel,
)