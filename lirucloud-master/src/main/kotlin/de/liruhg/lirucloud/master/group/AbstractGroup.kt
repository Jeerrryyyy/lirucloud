package de.liruhg.lirucloud.master.group

abstract class AbstractGroup(
    val name: String,
    val minServersOnline: Int,
    val maxMemory: Int,
    val minMemory: Int,
    val maxPlayers: Int,
)