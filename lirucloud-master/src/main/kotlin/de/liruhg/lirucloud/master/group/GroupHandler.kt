package de.liruhg.lirucloud.master.group

abstract class GroupHandler<T : AbstractGroup> {

    val groups: MutableMap<String, T> = mutableMapOf()

    fun registerGroup(group: T) {
        if (this.groups.containsKey(group.name)) return

        this.groups[group.name] = group
    }

    fun unregisterGroup(group: T) {
        if (!this.groups.containsKey(group.name)) return

        this.groups.remove(group.name)
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
    abstract fun groupExists(name: String): Boolean
    abstract fun shouldCreateGroup(): Boolean
    abstract fun fetchGroups(): Set<T>
}