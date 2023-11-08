package de.liruhg.lirucloud.library.cache.extension

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.liruhg.lirucloud.library.cache.CachePrefix
import redis.clients.jedis.JedisPooled

val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

fun <T> JedisPooled.insertEntity(prefix: CachePrefix, key: String, entity: T) =
    set("${prefix.prefix}:$key", gson.toJson(entity))

fun <T> JedisPooled.insertEntity(prefix: CachePrefix, key: String, entity: T, ttl: Long) =
    setex("${prefix.prefix}:$key", ttl, gson.toJson(entity))

fun JedisPooled.deleteEntity(prefix: CachePrefix, key: String) = del("${prefix.prefix}:$key")

fun JedisPooled.existsEntity(prefix: CachePrefix, key: String) = exists("${prefix.prefix}:$key")

inline fun <reified T> JedisPooled.getEntity(prefix: CachePrefix, key: String): T? =
    gson.fromJson(get("${prefix.prefix}:$key"), T::class.java)

fun JedisPooled.deleteEntities(pattern: String) = del(*keys(pattern).toTypedArray())

inline fun <reified T> JedisPooled.getAllEntities(pattern: String): List<T> =
    keys(pattern).map { gson.fromJson(get(it), T::class.java) }

inline fun <reified T> JedisPooled.getAllEntities(pattern: String, limit: Int): List<T> =
    keys(pattern).take(limit).map { gson.fromJson(get(it), T::class.java) }

inline fun <reified T> JedisPooled.getAllEntities(pattern: String, limit: Int, skip: Int): List<T> =
    keys(pattern).take(limit).drop(skip).map { gson.fromJson(get(it), T::class.java) }