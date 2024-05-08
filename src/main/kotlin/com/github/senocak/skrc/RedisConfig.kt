package com.github.senocak.skrc

import com.github.senocak.skrc.AppConstants.CACHE_CATEGORY
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

@Configuration
@EnableCaching
class RedisConfig {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${app.redis.HOST}") private var host: String? = null
    @Value("\${app.redis.PORT}") private var port: Int = 0
    @Value("\${app.redis.PASSWORD}") private var password: String? = null
    @Value("\${app.redis.TIMEOUT}") private var timeout: Int = 0

    @Bean
    fun jedisPool(): JedisPool {
        log.debug("RedisConfig: host=$host, port=$port, password=$password, timeout=$timeout")
        return JedisPool(JedisPoolConfig(), host, port, timeout, password)
    }

    /**
     * Create JedisPool
     * @return JedisPool
     */
    val jedisPool: JedisPool?
        get() = Companion.jedisPool

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = host!!
        redisStandaloneConfiguration.setPassword(password)
        redisStandaloneConfiguration.port = port
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun cacheConfiguration(): RedisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()))

    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer =
        RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
            val configurationMap: MutableMap<String, RedisCacheConfiguration> = HashMap()
            configurationMap[CACHE_CATEGORY] =
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(15))
            builder.withInitialCacheConfigurations(configurationMap)
        }

    companion object {
        private var jedisPool: JedisPool? = null
    }
}