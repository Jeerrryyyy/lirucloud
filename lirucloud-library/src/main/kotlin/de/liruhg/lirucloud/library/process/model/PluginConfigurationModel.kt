package de.liruhg.lirucloud.library.process.model

import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel

data class PluginConfigurationModel(
    val masterAddress: String,
    val masterPort: Int,
    val database: DatabaseConnectionModel,
    val processInformation: ProcessInformationModel
)