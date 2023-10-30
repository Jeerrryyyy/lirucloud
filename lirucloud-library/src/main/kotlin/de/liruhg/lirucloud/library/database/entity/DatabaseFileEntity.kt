package de.liruhg.lirucloud.library.database.entity

data class DatabaseFileEntity(
    val name: String,
    val nameHash: String,
    val fileObjectId: String,
    val updateDate: Long
)