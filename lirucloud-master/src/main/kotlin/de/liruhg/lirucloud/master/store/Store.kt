package de.liruhg.lirucloud.master.store

import de.liruhg.lirucloud.master.configuration.model.CloudConfigurationModel
import kotlin.properties.Delegates

class Store {

    lateinit var cloudConfiguration: CloudConfigurationModel
    lateinit var clientKey: String

    var debug by Delegates.notNull<Boolean>()
}