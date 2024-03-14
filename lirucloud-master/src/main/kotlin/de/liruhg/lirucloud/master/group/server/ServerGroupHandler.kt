package de.liruhg.lirucloud.master.group.server

import com.mongodb.client.model.Filters
import de.liruhg.lirucloud.library.cache.CacheConnectionFactory
import de.liruhg.lirucloud.library.cache.CachePrefix
import de.liruhg.lirucloud.library.cache.extension.deleteEntity
import de.liruhg.lirucloud.library.cache.extension.getAllEntities
import de.liruhg.lirucloud.library.cache.extension.getEntity
import de.liruhg.lirucloud.library.cache.extension.insertEntity
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.extension.deleteEntity
import de.liruhg.lirucloud.library.database.extension.getAllEntities
import de.liruhg.lirucloud.library.database.extension.getEntity
import de.liruhg.lirucloud.library.database.extension.insertEntity
import de.liruhg.lirucloud.library.database.handler.FileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import de.liruhg.lirucloud.master.group.GroupHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

class ServerGroupHandler(
    private val fileHandler: FileHandler,
    private val databaseConnectionFactory: DatabaseConnectionFactory,
    private val cacheConnectionFactory: CacheConnectionFactory
) : GroupHandler<ServerGroup> {

    private val logger: Logger = LoggerFactory.getLogger(ServerGroupHandler::class.java)

    override fun createGroup(group: ServerGroup) {
        this.databaseConnectionFactory.serverGroupsCollection.insertEntity(group)

        this.registerGroup(group)

        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${group.name}")
        val defaultTemplatePath = Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${group.name}/default")

        FileUtils.createDirectory(templatePath)
        FileUtils.createDirectory(defaultTemplatePath)
        FileUtils.createDirectory(File(defaultTemplatePath.toFile(), "plugins").toPath())

        FileUtils.copyAllFiles(
            File(Directories.MASTER_SOFTWARE_SERVER_PLUGINS).toPath(),
            File(defaultTemplatePath.toFile(), "plugins").path
        )

        FileUtils.writeStringToFile(
            File(defaultTemplatePath.toFile(), "LICENSE.txt"),
            "This server is provided by LiruCloud entirely written by Jevzo (voidptr). You are not allowed to redistribute this software or claim it as your own."
        )

        FileUtils.copyAllFiles(
            templatePath,
            "${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}"
        )
        FileUtils.zipDirectory(
            "${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}",
            File("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}.zip")
        )

        FileUtils.deleteFullDirectory(File("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}"))

        val hashedName = HashUtils.hashStringMD5(group.name)
        this.fileHandler.uploadFile(File(Directories.MASTER_TEMPLATE_SERVER_TEMP, "${group.name}.zip"), hashedName)

        this.logger.info("Successfully created group with Name: [${group.name}]")
    }

    override fun deleteGroup(group: ServerGroup) {
        val hashedName = HashUtils.hashStringMD5(group.name)

        this.fileHandler.deleteFile(hashedName)

        FileUtils.deleteFullDirectory(Path.of("${Directories.MASTER_TEMPLATE_SERVER}/${group.name}"))
        FileUtils.deleteIfExists(Path.of("${Directories.MASTER_TEMPLATE_SERVER_TEMP}/${group.name}.zip"))

        this.databaseConnectionFactory.serverGroupsCollection.deleteEntity(Filters.eq("name", group.name))
        this.unregisterGroup(group)

        this.logger.info("Successfully deleted group with Name: [${group.name}]")
    }

    override fun groupExists(name: String): Boolean {
        return this.databaseConnectionFactory.serverGroupsCollection.getEntity<ServerGroup>(
            Filters.eq(
                "name",
                name
            )
        ) != null
    }

    override fun shouldCreateGroup(): Boolean {
        return this.databaseConnectionFactory.serverGroupsCollection.countDocuments() == 0L
    }

    override fun fetchGroups(): Set<ServerGroup> {
        return this.databaseConnectionFactory.serverGroupsCollection.getAllEntities<ServerGroup>().toSet()
    }

    override fun fetchGroup(name: String): ServerGroup? {
        return this.databaseConnectionFactory.serverGroupsCollection.getEntity(Filters.eq("name", name))
    }

    override fun registerGroup(group: ServerGroup) {
        this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.SERVER_GROUP, group.name, group)
    }

    override fun unregisterGroup(group: ServerGroup) {
        this.cacheConnectionFactory.jedisPooled.deleteEntity(CachePrefix.SERVER_GROUP, group.name)
    }

    override fun getGroup(name: String): ServerGroup? {
        return this.cacheConnectionFactory.jedisPooled.getEntity(CachePrefix.SERVER_GROUP, name)
    }

    override fun getGroups(): List<ServerGroup> {
        return this.cacheConnectionFactory.jedisPooled.getAllEntities("${CachePrefix.SERVER_GROUP.prefix}:*")
    }

    override fun updateGroup(name: String) {
        val group = this.fetchGroup(name) ?: return

        this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.SERVER_GROUP, name, group)

    }

    override fun updateGroups() {
        this.fetchGroups().forEach {
            this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.SERVER_GROUP, it.name, it)
        }
    }
}