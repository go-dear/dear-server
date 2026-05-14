package com.dear

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class DearApplication

fun main(args: Array<String>) {
    runApplication<DearApplication>(*args)
}
