package de.liruhg.lirucloud.library.proxy

import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel

data class ProxyPluginConfigurationModel(
    val masterAddress: String,
    val masterPort: Int,
    val database: DatabaseConnectionModel,
    val proxyInformation: ProxyInformationModel
)