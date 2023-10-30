package de.liruhg.lirucloud.client.runtime

import de.liruhg.lirucloud.client.configuration.model.CloudConfigurationModel
import io.netty.channel.Channel
import kotlin.properties.Delegates

class RuntimeVars {
    lateinit var cloudConfiguration: CloudConfigurationModel
    lateinit var clientKey: String
    lateinit var webKey: String
    lateinit var masterChannel: Channel

    var debug by Delegates.notNull<Boolean>()
}