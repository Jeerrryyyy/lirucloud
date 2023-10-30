package de.liruhg.lirucloud.library.database.extension

import com.mongodb.client.MongoCollection
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.model.GridFSUploadOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import java.io.File

fun GridFSBucket.uploadZipFile(file: File, fileNameHash: String): ObjectId =
    uploadFromStream(fileNameHash, file.inputStream(), GridFSUploadOptions().metadata(Document("type", "zip archive")))

fun GridFSBucket.downloadZipFile(fileNameHash: String, file: File) = downloadToStream(fileNameHash, file.outputStream())

fun GridFSBucket.deleteZipFile(objectId: ObjectId) = delete(objectId)

fun <T> MongoCollection<Document>.insertEntity(entity: T): InsertOneResult =
    insertOne(gson.fromJson(gson.toJson(entity), Document::class.java))

fun <T> MongoCollection<Document>.replaceEntity(entity: T, filter: Bson): UpdateResult =
    replaceOne(filter, gson.fromJson(gson.toJson(entity), Document::class.java))

fun MongoCollection<Document>.deleteEntity(filter: Bson): DeleteResult = deleteOne(filter)

inline fun <reified T> MongoCollection<Document>.getEntity(filter: Bson): T? =
    gson.fromJson(find(filter).first()?.toJson(), T::class.java)

inline fun <reified T> MongoCollection<Document>.getEntities(filter: Bson): List<T> =
    find(filter).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getEntities(filter: Bson, limit: Int): List<T> =
    find(filter).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getEntities(filter: Bson, limit: Int, skip: Int): List<T> =
    find(filter).limit(limit).skip(skip).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getEntities(filter: Bson, sort: Bson): List<T> =
    find(filter).sort(sort).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getEntities(filter: Bson, sort: Bson, limit: Int): List<T> =
    find(filter).sort(sort).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getEntities(filter: Bson, sort: Bson, limit: Int, skip: Int): List<T> =
    find(filter).limit(limit).skip(skip).sort(sort).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getAllEntities(): List<T> =
    find().toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getAllEntities(limit: Int): List<T> =
    find().limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getAllEntities(skip: Int, limit: Int): List<T> =
    find().skip(skip).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getAllEntities(sort: Bson): List<T> =
    find().sort(sort).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getAllEntities(sort: Bson, limit: Int): List<T> =
    find().sort(sort).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }

inline fun <reified T> MongoCollection<Document>.getAllEntities(sort: Bson, limit: Int, skip: Int): List<T> =
    find().sort(sort).skip(skip).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }