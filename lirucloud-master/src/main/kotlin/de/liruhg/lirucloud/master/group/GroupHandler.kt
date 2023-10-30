package de.liruhg.lirucloud.master.group

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class GroupHandler<T : AbstractGroup> {

    val logger: Logger = LoggerFactory.getLogger(GroupHandler::class.java)
    val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val groups: MutableMap<String, T> = mutableMapOf()

    fun registerGroup(group: T) {
        if (this.groups.containsKey(group.name)) return

        this.groups[group.name] = group
        this.logger.info("Successfully registered group with Name: [${group.name}]")
    }

    fun editGroup(group: T) {
        this.deleteGroup(group)
        this.createGroup(group)
    }

    fun getGroup(name: String): T? {
        return this.groups[name]
    }

    fun getGroups(): MutableSet<T> {
        return this.groups.values.toMutableSet()
    }

    abstract fun createGroup(group: T)
    abstract fun deleteGroup(group: T)
}