package de.liruhg.lirucloud.client.process.server.model

import de.liruhg.lirucloud.client.process.InternalCloudProcess
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.process.ServerMode
import java.nio.file.Path

class InternalServerProcess(
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
    serverDirectoryPath: Path,
    process: Process,
    processStreamConsumer: ProcessStreamConsumer,
    val mode: ServerMode
) : InternalCloudProcess(
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
    serverDirectoryPath,
    process,
    processStreamConsumer
)