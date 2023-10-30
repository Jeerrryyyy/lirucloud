package de.liruhg.lirucloud.client.process

interface ProcessRequestHandler<T> {

    fun handle(request: T)
}