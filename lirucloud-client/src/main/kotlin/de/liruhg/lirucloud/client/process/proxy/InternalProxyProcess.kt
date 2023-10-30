package de.liruhg.lirucloud.client.process.proxy

import de.liruhg.lirucloud.client.process.InternalCloudProcess
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.ProcessType
import java.nio.file.Path

class InternalProxyProcess(
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
    joinPower: Int,
    maintenance: Boolean,
    serverDirectoryPath: Path,
    process: Process,
    processStreamConsumer: ProcessStreamConsumer,
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
    joinPower,
    maintenance,
    serverDirectoryPath,
    process,
    processStreamConsumer
)