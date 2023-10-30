package de.liruhg.lirucloud.library.configuration.model

data class DatabaseConnectionModel(
    val connectionUrl: String,
    val databaseName: String,
    val bucketName: String,
    val collections: MutableMap<String, String>,
)