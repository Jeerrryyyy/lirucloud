package de.liruhg.lirucloud.library.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.GridFSBuckets
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel
import org.bson.Document

class DatabaseConnectionFactory {

    lateinit var gridFsBucket: GridFSBucket
    lateinit var filesCollection: MongoCollection<Document>
    lateinit var proxyGroupsCollection: MongoCollection<Document>
    lateinit var serverGroupsCollection: MongoCollection<Document>

    fun connectDatabase(databaseConnectionModel: DatabaseConnectionModel) {
        val mongoClient: MongoClient = MongoClients.create(databaseConnectionModel.connectionUrl)
        val mongoDatabase: MongoDatabase = mongoClient.getDatabase(databaseConnectionModel.databaseName)

        this.gridFsBucket = GridFSBuckets.create(mongoDatabase, databaseConnectionModel.bucketName)

        databaseConnectionModel.collections.forEach { (identifier, collectionName) ->
            when (identifier) {
                "filesCollection" -> this.filesCollection = mongoDatabase.getCollection(collectionName)
                "proxyGroupsCollection" -> this.proxyGroupsCollection = mongoDatabase.getCollection(collectionName)
                "serverGroupsCollection" -> this.serverGroupsCollection = mongoDatabase.getCollection(collectionName)
            }
        }
    }
}