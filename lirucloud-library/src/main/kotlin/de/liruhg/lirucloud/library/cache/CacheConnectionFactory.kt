package de.liruhg.lirucloud.library.cache

import de.liruhg.lirucloud.library.configuration.model.CacheConnection
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPooled

class CacheConnectionFactory {

    lateinit var jedisPooled: JedisPooled

    fun connectCache(cacheConnection: CacheConnection) {
        this.jedisPooled = JedisPooled(
            HostAndPort(cacheConnection.host, cacheConnection.port),
            DefaultJedisClientConfig.builder()
                .user(cacheConnection.user)
                .password(cacheConnection.password)
                .database(cacheConnection.database)
                .build()
        )
    }
}