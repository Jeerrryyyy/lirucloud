package de.liruhg.lirucloud.client.process

import de.liruhg.lirucloud.library.process.ProcessMode
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.ProcessType
import java.nio.file.Path

class InternalCloudProcess(
    val groupName: String,
    val name: String?,
    val uuid: String?,
    val ip: String,
    val type: ProcessType,
    var stage: ProcessStage,
    var mode: ProcessMode,
    val minMemory: Int,
    val maxMemory: Int,
    val port: Int,
    val maxPlayers: Int,
    val serverDirectoryPath: Path,
    val process: Process,
    val processStreamConsumer: ProcessStreamConsumer
)