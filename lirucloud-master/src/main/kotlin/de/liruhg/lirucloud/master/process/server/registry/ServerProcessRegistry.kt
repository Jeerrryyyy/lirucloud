package de.liruhg.lirucloud.master.process.server.registry

import de.liruhg.lirucloud.library.process.ServerMode
import de.liruhg.lirucloud.library.process.model.ServerProcess
import de.liruhg.lirucloud.master.process.ProcessRegistry

class ServerProcessRegistry : ProcessRegistry<ServerProcess>() {

    fun getServerProcessesToRegister(): MutableMap<String, Pair<String, Int>> {
        return this.processes.values.filter { it.mode != ServerMode.LOBBY }
            .associate { Pair(it.name!!, Pair(it.ip, it.port)) }
            .toMutableMap()
    }

    fun getLobbyProcessesToRegister(): MutableMap<String, Pair<String, Int>> {
        return this.processes.values.filter { it.mode == ServerMode.LOBBY }
            .associate { Pair(it.name!!, Pair(it.ip, it.port)) }
            .toMutableMap()
    }
}