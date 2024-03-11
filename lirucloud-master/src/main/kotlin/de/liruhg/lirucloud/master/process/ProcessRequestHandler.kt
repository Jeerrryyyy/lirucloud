package de.liruhg.lirucloud.master.process

import de.liruhg.lirucloud.library.process.CloudProcess

interface ProcessRequestHandler {

    fun requestProcess(process: CloudProcess)
    fun requestProcesses(count: Int, process: CloudProcess)
}