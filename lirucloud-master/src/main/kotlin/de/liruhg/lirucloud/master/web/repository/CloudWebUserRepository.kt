package de.liruhg.lirucloud.master.web.repository

import com.mongodb.client.model.Filters
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.extension.deleteEntity
import de.liruhg.lirucloud.library.database.extension.getEntity
import de.liruhg.lirucloud.library.database.extension.insertEntity
import de.liruhg.lirucloud.library.database.extension.replaceEntity
import de.liruhg.lirucloud.library.user.CloudWebUser
import de.liruhg.lirucloud.library.util.HashUtils

class CloudWebUserRepository(
    private val databaseConnectionFactory: DatabaseConnectionFactory
) {

    fun createCloudWebUser(cloudWebUser: CloudWebUser) {
        this.databaseConnectionFactory.webUserCollection.insertEntity(
            cloudWebUser.copy(
                password = HashUtils.hashStringBcrypt(
                    cloudWebUser.password
                )
            )
        )
    }

    fun updateCloudWebUser(cloudWebUser: CloudWebUser) {
        this.databaseConnectionFactory.webUserCollection.replaceEntity(
            cloudWebUser.copy(
                password = HashUtils.hashStringBcrypt(
                    cloudWebUser.password
                )
            ),
            Filters.eq("id", cloudWebUser.id)
        )
    }

    fun getCloudWebUser(email: String): CloudWebUser? {
        return this.databaseConnectionFactory.webUserCollection.getEntity(Filters.eq("email", email))
    }

    fun deleteCloudWebUser(id: String) {
        this.databaseConnectionFactory.webUserCollection.deleteEntity(Filters.eq("id", id))
    }
}