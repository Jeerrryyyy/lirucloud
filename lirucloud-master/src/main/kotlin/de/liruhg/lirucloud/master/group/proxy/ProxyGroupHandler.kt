package de.liruhg.lirucloud.master.group.proxy

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
import de.liruhg.lirucloud.master.group.proxy.model.ProxyGroupModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

class ProxyGroupHandler(
    private val fileHandler: FileHandler,
    private val databaseConnectionFactory: DatabaseConnectionFactory,
    private val cacheConnectionFactory: CacheConnectionFactory
) : GroupHandler<ProxyGroupModel> {

    private val logger: Logger = LoggerFactory.getLogger(ProxyGroupHandler::class.java)

    override fun createGroup(group: ProxyGroupModel) {
        this.databaseConnectionFactory.proxyGroupsCollection.insertEntity(group)

        this.registerGroup(group)

        val templatePath = Path.of("${Directories.MASTER_TEMPLATE_PROXY}/${group.name}")
        val defaultTemplatePath = Path.of("${Directories.MASTER_TEMPLATE_PROXY}/${group.name}/default")

        FileUtils.createDirectory(templatePath)
        FileUtils.createDirectory(defaultTemplatePath)
        FileUtils.createDirectory(File(defaultTemplatePath.toFile(), "plugins").toPath())

        FileUtils.copyAllFiles(
            File(Directories.MASTER_SOFTWARE_PROXY_PLUGINS).toPath(),
            File(defaultTemplatePath.toFile(), "plugins").path
        )

        FileUtils.writeStringToFile(
            File(defaultTemplatePath.toFile(), "LICENSE.txt"),
            "This server is provided by LiruCloud entirely written by JevzoTV. You are not allowed to redistribute this software or claim it as your own."
        )

        FileUtils.copyAllFiles(
            templatePath,
            "${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${group.name}"
        )
        FileUtils.zipDirectory(
            "${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${group.name}",
            File("${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${group.name}.zip")
        )

        FileUtils.deleteFullDirectory(File("${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${group.name}"))

        val hashedName = HashUtils.hashStringMD5(group.name)
        this.fileHandler.uploadFile(File(Directories.MASTER_TEMPLATE_PROXY_TEMP, "${group.name}.zip"), hashedName)

        this.logger.info("Successfully created group with Name: [${group.name}]")
    }

    override fun deleteGroup(group: ProxyGroupModel) {
        this.fileHandler.deleteFile(HashUtils.hashStringMD5(group.name))

        FileUtils.deleteFullDirectory(Path.of("${Directories.MASTER_TEMPLATE_PROXY}/${group.name}"))
        FileUtils.deleteIfExists(Path.of("${Directories.MASTER_TEMPLATE_PROXY_TEMP}/${group.name}.zip"))

        this.databaseConnectionFactory.proxyGroupsCollection.deleteEntity(Filters.eq("name", group.name))
        this.unregisterGroup(group)

        this.logger.info("Successfully deleted group with Name: [${group.name}]")
    }

    override fun groupExists(name: String): Boolean {
        return this.databaseConnectionFactory.proxyGroupsCollection.getEntity<ProxyGroupModel>(
            Filters.eq(
                "name",
                name
            )
        ) != null
    }

    override fun shouldCreateGroup(): Boolean {
        return this.databaseConnectionFactory.proxyGroupsCollection.countDocuments() == 0L
    }

    override fun fetchGroups(): Set<ProxyGroupModel> {
        return this.databaseConnectionFactory.proxyGroupsCollection.getAllEntities<ProxyGroupModel>().toSet()
    }

    override fun fetchGroup(name: String): ProxyGroupModel? {
        return this.databaseConnectionFactory.proxyGroupsCollection.getEntity(Filters.eq("name", name))
    }

    override fun registerGroup(group: ProxyGroupModel) {
        this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.PROXY_GROUP, group.name, group)
    }

    override fun unregisterGroup(group: ProxyGroupModel) {
        this.cacheConnectionFactory.jedisPooled.deleteEntity(CachePrefix.PROXY_GROUP, group.name)
    }

    override fun getGroup(name: String): ProxyGroupModel? {
        return this.cacheConnectionFactory.jedisPooled.getEntity(CachePrefix.PROXY_GROUP, name)
    }

    override fun getGroups(): List<ProxyGroupModel> {
        return this.cacheConnectionFactory.jedisPooled.getAllEntities("${CachePrefix.PROXY_GROUP.prefix}:*")
    }

    override fun updateGroup(name: String) {
        val group = this.fetchGroup(name) ?: return

        this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.PROXY_GROUP, name, group)
    }

    override fun updateGroups() {
        this.fetchGroups().forEach {
            this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.PROXY_GROUP, it.name, it)
        }
    }
}