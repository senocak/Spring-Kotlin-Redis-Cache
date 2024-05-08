package com.github.senocak.skrc

import java.net.InetSocketAddress
import net.spy.memcached.MemcachedClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener

@SpringBootApplication
class SpringKotlinRedisCacheApplication {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @EventListener(value = [ApplicationReadyEvent::class])
    fun init(event: ApplicationReadyEvent) {
        val mcc = MemcachedClient(InetSocketAddress("127.0.0.1", 11211))
        mcc.set("anil", 900, "senocak").isDone
        mcc.get("anil").run { log.info("Key: $this") }
    }
}

fun main(args: Array<String>) {
    runApplication<SpringKotlinRedisCacheApplication>(*args)
}
