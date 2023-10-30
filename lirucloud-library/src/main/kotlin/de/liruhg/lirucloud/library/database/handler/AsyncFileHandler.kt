package de.liruhg.lirucloud.library.database.handler

import com.mongodb.client.model.Filters
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.entity.DatabaseFileEntity
import de.liruhg.lirucloud.library.database.extension.*
import org.bson.types.ObjectId
import java.io.File

class AsyncFileHandler(
    private val databaseConnectionFactory: DatabaseConnectionFactory
) : FileHandler {

    override fun uploadFile(file: File, fileNameHash: String) {
        this.databaseConnectionFactory.filesCollection.getEntityAsync<DatabaseFileEntity>(
            Filters.eq(
                "nameHash",
                fileNameHash
            )
        ).thenAcceptAsync {
            if (it != null) {
                this.replaceFile(file, fileNameHash)
                return@thenAcceptAsync
            }

            this.databaseConnectionFactory.gridFsBucket.uploadZipFileAsync(file, fileNameHash).thenAcceptAsync { id ->
                this.databaseConnectionFactory.filesCollection.insertEntityAsync(
                    DatabaseFileEntity(
                        file.name,
                        fileNameHash,
                        id.toHexString(),
                        System.currentTimeMillis()
                    )
                )
            }
        }
    }

    override fun downloadFile(fileNameHash: String, file: File) {
        this.databaseConnectionFactory.gridFsBucket.downloadZipFileAsync(fileNameHash, file)
    }

    override fun deleteFile(fileNameHash: String) {
        this.databaseConnectionFactory.filesCollection.getEntityAsync<DatabaseFileEntity>(
            Filters.eq(
                "nameHash",
                fileNameHash
            )
        ).thenAcceptAsync {
            if (it == null) {
                return@thenAcceptAsync
            }

            this.databaseConnectionFactory.gridFsBucket.deleteZipFileAsync(ObjectId(it.fileObjectId)).thenAcceptAsync {
                this.databaseConnectionFactory.filesCollection.deleteEntityAsync(Filters.eq("nameHash", fileNameHash))
            }
        }
    }

    override fun replaceFile(file: File, fileNameHash: String) {
        this.databaseConnectionFactory.filesCollection.getEntityAsync<DatabaseFileEntity>(
            Filters.eq(
                "nameHash",
                fileNameHash
            )
        ).thenAcceptAsync {
            if (it == null) {
                return@thenAcceptAsync
            }

            this.databaseConnectionFactory.gridFsBucket.deleteZipFileAsync(ObjectId(it.fileObjectId)).thenAcceptAsync {
                this.databaseConnectionFactory.gridFsBucket.uploadZipFileAsync(file, fileNameHash)
                    .thenAcceptAsync { id ->
                        this.databaseConnectionFactory.filesCollection.replaceEntityAsync(
                            DatabaseFileEntity(file.name, fileNameHash, id.toHexString(), System.currentTimeMillis()),
                            Filters.eq("nameHash", fileNameHash)
                        )
                    }
            }
        }
    }
}