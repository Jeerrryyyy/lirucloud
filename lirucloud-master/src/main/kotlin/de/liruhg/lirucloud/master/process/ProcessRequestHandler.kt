package de.liruhg.lirucloud.master.process

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.process.AbstractProcess

interface ProcessRequestHandler<T : AbstractProcess> {

    fun requestProcessesOnConnect(clientInfoModel: ClientInfoModel)
    fun requestProcess(process: T)
    fun requestProcesses(count: Int, process: T)
}