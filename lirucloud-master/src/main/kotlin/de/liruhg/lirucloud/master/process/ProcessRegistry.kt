package de.liruhg.lirucloud.master.process

import de.liruhg.lirucloud.library.cache.CacheConnectionFactory
import de.liruhg.lirucloud.library.cache.CachePrefix
import de.liruhg.lirucloud.library.cache.extension.*
import de.liruhg.lirucloud.library.process.CloudProcess
import io.netty.channel.Channel

class ProcessRegistry(
    private val cacheConnectionFactory: CacheConnectionFactory
) {

    private val channels: MutableMap<String, Channel> = mutableMapOf()

    fun addProcess(process: CloudProcess) {
        this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.PROCESS, process.uuid!!, process)
    }

    fun removeProcess(process: CloudProcess) {
        this.removeChannel(process.uuid!!)
        this.cacheConnectionFactory.jedisPooled.deleteEntity(CachePrefix.PROCESS, process.uuid!!)
    }

    fun updateProcess(process: CloudProcess) {
        this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.PROCESS, process.uuid!!, process)
    }

    fun containsProcess(uuid: String): Boolean {
        return this.cacheConnectionFactory.jedisPooled.existsEntity(CachePrefix.PROCESS, uuid)
    }

    fun getProcess(uuid: String): CloudProcess? {
        return this.cacheConnectionFactory.jedisPooled.getEntity(CachePrefix.PROCESS, uuid)
    }

    fun getRunningProcessCount(groupName: String): Int {
        return this.cacheConnectionFactory.jedisPooled.getAllEntities<CloudProcess>("${CachePrefix.PROCESS.prefix}:*")
            .count { it.groupName == groupName }
    }

    fun getRunningProcessCount(): Int {
        return this.cacheConnectionFactory.jedisPooled.getAllEntities<CloudProcess>("${CachePrefix.PROCESS.prefix}:*")
            .count()
    }

    fun getProcessByChannel(channel: Channel): CloudProcess? {
        val uuid = this.channels.filter { it.value == channel }.keys.firstOrNull() ?: return null

        return this.cacheConnectionFactory.jedisPooled.getEntity<CloudProcess>(CachePrefix.PROCESS, uuid)
    }

    fun getProcesses(): List<CloudProcess> {
        return this.cacheConnectionFactory.jedisPooled.getAllEntities<CloudProcess>("${CachePrefix.PROCESS.prefix}:*")
    }

    fun getProcesses(groupName: String): List<CloudProcess> {
        return this.cacheConnectionFactory.jedisPooled.getAllEntities<CloudProcess>("${CachePrefix.PROCESS.prefix}:*")
            .filter { it.groupName == groupName }
    }

    fun addChannel(uuid: String, channel: Channel) {
        this.channels[uuid] = channel
    }

    fun removeChannel(uuid: String) {
        this.channels.remove(uuid)
    }

    fun updateChannel(uuid: String, channel: Channel) {
        this.channels[uuid] = channel
    }

    fun getChannel(uuid: String): Channel? {
        return this.channels[uuid]
    }
}