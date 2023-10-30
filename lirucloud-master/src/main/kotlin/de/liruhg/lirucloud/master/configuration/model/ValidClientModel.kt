package de.liruhg.lirucloud.master.configuration.model

data class ValidClientModel(
    val clientName: String,
    val whitelistedIps: Set<String>
)