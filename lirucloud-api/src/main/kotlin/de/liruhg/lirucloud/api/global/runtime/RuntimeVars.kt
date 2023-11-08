package de.liruhg.lirucloud.api.global.runtime

import de.liruhg.lirucloud.library.process.model.PluginConfigurationModel
import io.netty.channel.Channel

class RuntimeVars {
    lateinit var pluginConfiguration: PluginConfigurationModel
    lateinit var clientKey: String
    lateinit var masterChannel: Channel
}