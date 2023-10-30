package de.liruhg.lirucloud.library.configuration

class ConfigurationExecutor {

    private val configurations: MutableSet<Configuration> = mutableSetOf()

    fun registerConfiguration(configuration: Configuration) {
        this.configurations.add(configuration)
    }

    fun executeConfigurations(vararg configurations: Configuration) {
        configurations.forEach { it.execute() }
    }

    fun executeConfigurations() {
        this.configurations.forEach { it.execute() }
    }
}