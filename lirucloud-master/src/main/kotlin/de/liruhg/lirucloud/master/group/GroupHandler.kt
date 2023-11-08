package de.liruhg.lirucloud.master.group

interface GroupHandler<T : AbstractGroup> {

    fun registerGroup(group: T)
    fun unregisterGroup(group: T)
    fun createGroup(group: T)
    fun deleteGroup(group: T)
    fun groupExists(name: String): Boolean
    fun shouldCreateGroup(): Boolean
    fun fetchGroups(): Set<T>
    fun fetchGroup(name: String): T?
    fun getGroup(name: String): T?
    fun getGroups(): List<T>
    fun updateGroups()
    fun updateGroup(name: String)
}