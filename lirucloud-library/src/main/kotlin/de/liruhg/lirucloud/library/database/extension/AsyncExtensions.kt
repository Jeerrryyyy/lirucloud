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
import java.util.concurrent.CompletableFuture

fun GridFSBucket.uploadZipFileAsync(file: File, fileNameHash: String): CompletableFuture<ObjectId> {
    return CompletableFuture.supplyAsync({
        uploadFromStream(
            fileNameHash,
            file.inputStream(),
            GridFSUploadOptions().metadata(Document("type", "zip archive"))
        )
    }, threadPool)
}

fun GridFSBucket.downloadZipFileAsync(fileNameHash: String, file: File) {
    CompletableFuture.runAsync({
        downloadToStream(fileNameHash, file.outputStream())
    }, threadPool)
}

fun GridFSBucket.deleteZipFileAsync(objectId: ObjectId): CompletableFuture<Void> {
    return CompletableFuture.runAsync({
        delete(objectId)
    }, threadPool)
}

fun <T> MongoCollection<Document>.insertEntityAsync(entity: T): CompletableFuture<InsertOneResult> {
    return CompletableFuture.supplyAsync({
        insertOne(gson.fromJson(gson.toJson(entity), Document::class.java))
    }, threadPool)
}

fun <T> MongoCollection<Document>.replaceEntityAsync(entity: T, filter: Bson): CompletableFuture<UpdateResult> {
    return CompletableFuture.supplyAsync({
        replaceOne(filter, gson.fromJson(gson.toJson(entity), Document::class.java))
    }, threadPool)
}

fun MongoCollection<Document>.deleteEntityAsync(filter: Bson): CompletableFuture<DeleteResult> {
    return CompletableFuture.supplyAsync({
        deleteOne(filter)
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getEntityAsync(filter: Bson): CompletableFuture<T?> {
    return CompletableFuture.supplyAsync({
        gson.fromJson(find(filter).first()?.toJson(), T::class.java)
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getEntitiesAsync(filter: Bson): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find(filter).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getEntitiesAsync(
    filter: Bson,
    limit: Int
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find(filter).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getEntitiesAsync(
    filter: Bson,
    limit: Int,
    skip: Int
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find(filter).limit(limit).skip(skip).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getEntitiesAsync(
    filter: Bson,
    sort: Bson
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find(filter).sort(sort).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getEntitiesAsync(
    filter: Bson,
    sort: Bson,
    limit: Int
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find(filter).sort(sort).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getEntitiesAsync(
    filter: Bson,
    sort: Bson,
    limit: Int,
    skip: Int
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find(filter).limit(limit).skip(skip).sort(sort).toList()
            .map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getAllEntitiesAsync(): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find().toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getAllEntitiesAsync(limit: Int): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find().limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getAllEntitiesAsync(
    skip: Int,
    limit: Int
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find().skip(skip).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getAllEntitiesAsync(sort: Bson): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find().sort(sort).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getAllEntitiesAsync(
    sort: Bson,
    limit: Int
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find().sort(sort).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}

inline fun <reified T> MongoCollection<Document>.getAllEntitiesAsync(
    sort: Bson,
    limit: Int,
    skip: Int
): CompletableFuture<List<T>> {
    return CompletableFuture.supplyAsync({
        find().sort(sort).skip(skip).limit(limit).toList().map { gson.fromJson(it.toJson(), T::class.java) }
    }, threadPool)
}