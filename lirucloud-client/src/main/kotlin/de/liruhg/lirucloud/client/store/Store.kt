package de.liruhg.lirucloud.client.store

import de.liruhg.lirucloud.client.configuration.model.CloudConfiguration
import io.netty.channel.Channel
import kotlin.properties.Delegates

class Store {

    lateinit var cloudConfiguration: CloudConfiguration
    lateinit var clientKey: String
    lateinit var masterChannel: Channel

    var debug by Delegates.notNull<Boolean>()
}