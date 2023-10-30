package de.liruhg.lirucloud.library.process

abstract class AbstractProcess(
    val groupName: String,
    var name: String?,
    var uuid: String?,
    val ip: String,
    val type: ProcessType,
    val stage: ProcessStage,
    val minMemory: Int,
    val maxMemory: Int,
    var port: Int,
    val maxPlayers: Int,
    val joinPower: Int,
    val maintenance: Boolean,
)