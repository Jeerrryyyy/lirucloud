package de.liruhg.lirucloud.master.configuration.model

data class ValidClient(
    val clientName: String,
    val whitelistedIps: Set<String>
)