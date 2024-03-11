package de.liruhg.lirucloud.master.store

import de.liruhg.lirucloud.master.configuration.model.CloudConfiguration
import kotlin.properties.Delegates

class Store {

    lateinit var cloudConfiguration: CloudConfiguration
    lateinit var clientKey: String

    var debug by Delegates.notNull<Boolean>()
}