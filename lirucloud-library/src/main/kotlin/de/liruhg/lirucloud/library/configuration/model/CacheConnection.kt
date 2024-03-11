package de.liruhg.lirucloud.library.configuration.model

data class CacheConnection(
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val database: Int,
)