package de.liruhg.lirucloud.library.cache

import de.liruhg.lirucloud.library.configuration.model.CacheConnectionModel
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPooled

class CacheConnectionFactory {

    lateinit var jedisPooled: JedisPooled

    fun connectCache(cacheConnectionModel: CacheConnectionModel) {
        this.jedisPooled = JedisPooled(
            HostAndPort(cacheConnectionModel.host, cacheConnectionModel.port),
            DefaultJedisClientConfig.builder()
                .user(cacheConnectionModel.user)
                .password(cacheConnectionModel.password)
                .database(cacheConnectionModel.database)
                .build()
        )
    }
}