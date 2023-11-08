package de.liruhg.lirucloud.client.process

import de.liruhg.lirucloud.library.process.CloudProcess

interface ProcessRequestHandler {

    fun handle(request: CloudProcess)
}