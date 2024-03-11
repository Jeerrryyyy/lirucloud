package de.liruhg.lirucloud.library.process

class CloudProcess(
    val groupName: String,
    var name: String?,
    var uuid: String?,
    val ip: String,
    val type: ProcessType,
    var stage: ProcessStage,
    var mode: ProcessMode,
    val minMemory: Int,
    val maxMemory: Int,
    var port: Int,
    val maxPlayers: Int
)