package com.example.infrastructure.eventbus

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import net.engio.mbassy.bus.MBassador
import javax.inject.Singleton

@Factory
class EventBusFactory {
  @Bean(preDestroy = "shutdown")
  @Singleton
  fun createGeneric(): MBassador<Any> = MBassador()
}

