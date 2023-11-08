package de.liruhg.lirucloud.master.process.handler

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.process.CloudProcess

interface ProcessRequestHandler {

    fun requestProcessesOnConnect(clientInfoModel: ClientInfoModel)
    fun requestProcess(process: CloudProcess)
    fun requestProcesses(count: Int, process: CloudProcess)
}