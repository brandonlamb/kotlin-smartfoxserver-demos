package com.example.infrastructure.akka

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newCachedThreadPool
import java.util.concurrent.Executors.newFixedThreadPool
import javax.inject.Named
import javax.inject.Singleton

@Factory
class ThreadFactory {
  @Bean(preDestroy = "shutdown")
  @Singleton
  @Named("akka-es-1")
  fun createThreadPool1(): ExecutorService = newCachedThreadPool()

  @Bean(preDestroy = "shutdown")
  @Singleton
  @Named("akka-es-2")
  fun createThreadPool2(): ExecutorService = newFixedThreadPool(8)
}
