package com.example.infrastructure.config

import io.micronaut.context.annotation.ConfigurationProperties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfig @Inject constructor(
  val api: ApiConfig,
  val game: GameConfig,
  val dispatchers: AkkaDispatcherConfig,
  val requests: RequestConfig,
  val player: PlayerConfig
)

@ConfigurationProperties("app.api")
class ApiConfig {
  lateinit var apiKey: String
}

@ConfigurationProperties("app.game")
class GameConfig {
  lateinit var name: String
  lateinit var tick: String
}

@ConfigurationProperties("app.akka.dispatchers")
class AkkaDispatcherConfig {
  lateinit var forkJoin: String
  lateinit var pinned: String
  lateinit var affinity: String
  lateinit var default: String
}

@ConfigurationProperties("sfs2x.request-types")
class RequestConfig {
  lateinit var player: String
  lateinit var item: String
  lateinit var trade: String
}

@ConfigurationProperties("sfs2x.player")
class PlayerConfig {
  lateinit var move: String
  lateinit var attack: String
}
