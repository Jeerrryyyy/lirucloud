package de.liruhg.lirucloud.api.global.runtime

import de.liruhg.lirucloud.library.process.model.PluginConfigurationModel
import de.liruhg.lirucloud.library.proxy.ProxyInformationModel
import io.netty.channel.Channel

class RuntimeVars {
    lateinit var pluginConfiguration: PluginConfigurationModel
    lateinit var proxyInformation: ProxyInformationModel
    lateinit var clientKey: String
    lateinit var masterChannel: Channel
}