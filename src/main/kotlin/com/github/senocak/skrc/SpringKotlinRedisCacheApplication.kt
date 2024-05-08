package com.github.senocak.skrc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKotlinRedisCacheApplication

fun main(args: Array<String>) {
    runApplication<SpringKotlinRedisCacheApplication>(*args)
}
