package com.example.infrastructure.akka

import akka.actor.ActorSystem
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import javax.inject.Singleton

@Factory
class SystemFactory {
  @Bean(preDestroy = "terminate")
  @Singleton
  fun create(): ActorSystem = ActorSystem.create("system")
}
