package de.liruhg.lirucloud.api.proxy.runtime

import de.liruhg.lirucloud.library.proxy.ProxyPluginConfigurationModel
import io.netty.channel.Channel

class RuntimeVars {
    lateinit var proxyPluginConfigurationModel: ProxyPluginConfigurationModel
    lateinit var clientKey: String
    lateinit var masterChannel: Channel
}