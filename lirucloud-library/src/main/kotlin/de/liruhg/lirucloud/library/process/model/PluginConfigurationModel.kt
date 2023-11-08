package de.liruhg.lirucloud.library.process.model

import de.liruhg.lirucloud.library.configuration.model.CacheConnectionModel
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel

data class PluginConfigurationModel(
    val masterAddress: String,
    val masterPort: Int,
    val database: DatabaseConnectionModel,
    val cache: CacheConnectionModel,
    val processInformation: ProcessInformationModel
)