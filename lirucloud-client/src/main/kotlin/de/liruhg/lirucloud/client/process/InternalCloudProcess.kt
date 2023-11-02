package de.liruhg.lirucloud.client.process

import de.liruhg.lirucloud.library.process.AbstractProcess
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.ProcessType
import java.nio.file.Path

abstract class InternalCloudProcess(
    groupName: String,
    name: String?,
    uuid: String?,
    ip: String,
    type: ProcessType,
    stage: ProcessStage,
    minMemory: Int,
    maxMemory: Int,
    port: Int,
    maxPlayers: Int,
    val serverDirectoryPath: Path,
    val process: Process,
    val processStreamConsumer: ProcessStreamConsumer
) : AbstractProcess(
    groupName,
    name,
    uuid,
    ip,
    type,
    stage,
    minMemory,
    maxMemory,
    port,
    maxPlayers,
)