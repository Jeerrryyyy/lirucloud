package de.liruhg.lirucloud.library.database.handler

import com.mongodb.client.model.Filters
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.entity.DatabaseFileEntity
import de.liruhg.lirucloud.library.database.extension.*
import org.bson.types.ObjectId
import java.io.File

class SyncFileHandler(
    private val databaseConnectionFactory: DatabaseConnectionFactory
) : FileHandler {

    override fun uploadFile(file: File, fileNameHash: String) {
        val databaseFileEntity = this.databaseConnectionFactory.filesCollection.getEntity<DatabaseFileEntity>(
            Filters.eq("nameHash", fileNameHash)
        )

        if (databaseFileEntity != null) {
            this.replaceFile(file, fileNameHash)
            return
        }

        val fileObjectId = this.databaseConnectionFactory.gridFsBucket.uploadZipFile(file, fileNameHash)
        this.databaseConnectionFactory.filesCollection.insertEntity(
            DatabaseFileEntity(
                file.name,
                fileNameHash,
                fileObjectId.toHexString(),
                System.currentTimeMillis()
            )
        )
    }

    override fun downloadFile(fileNameHash: String, file: File) {
        this.databaseConnectionFactory.gridFsBucket.downloadZipFile(fileNameHash, file)
    }

    override fun deleteFile(fileNameHash: String) {
        val databaseFileEntity = this.databaseConnectionFactory.filesCollection.getEntity<DatabaseFileEntity>(
            Filters.eq("nameHash", fileNameHash)
        ) ?: return

        this.databaseConnectionFactory.gridFsBucket.deleteZipFile(ObjectId(databaseFileEntity.fileObjectId))
        this.databaseConnectionFactory.filesCollection.deleteEntity(Filters.eq("nameHash", fileNameHash))
    }

    override fun replaceFile(file: File, fileNameHash: String) {
        val databaseFileEntity = this.databaseConnectionFactory.filesCollection.getEntity<DatabaseFileEntity>(
            Filters.eq("nameHash", fileNameHash)
        ) ?: return

        this.databaseConnectionFactory.gridFsBucket.deleteZipFile(ObjectId(databaseFileEntity.fileObjectId))
        val fileObjectId = this.databaseConnectionFactory.gridFsBucket.uploadZipFile(file, fileNameHash)

        this.databaseConnectionFactory.filesCollection.replaceEntity(
            DatabaseFileEntity(file.name, fileNameHash, fileObjectId.toHexString(), System.currentTimeMillis()),
            Filters.eq("nameHash", fileNameHash)
        )
    }
}