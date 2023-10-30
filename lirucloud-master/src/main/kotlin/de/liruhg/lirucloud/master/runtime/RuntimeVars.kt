package de.liruhg.lirucloud.master.runtime

import de.liruhg.lirucloud.master.configuration.model.CloudConfigurationModel
import kotlin.properties.Delegates

class RuntimeVars {

    lateinit var cloudConfiguration: CloudConfigurationModel
    lateinit var clientKey: String
    lateinit var webKey: String

    var debug by Delegates.notNull<Boolean>()
}