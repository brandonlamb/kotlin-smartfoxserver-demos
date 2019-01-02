package com.example.domain.core

import akka.actor.AbstractLoggingActor
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.infrastructure.akka.TimeoutException
import com.example.infrastructure.akka.UnstableClientException
import com.example.infrastructure.config.AppConfig
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.MINUTES

class GameActor(config: AppConfig) : AbstractLoggingActor() {
  private val strategy: SupervisorStrategy = OneForOneStrategy(
    10,
    Duration.create(1, MINUTES),
    DeciderBuilder
      .match(TimeoutException::class.java) { restart() }
      .match(UnstableClientException::class.java) { stop() }
      .build()
  )

  private val game = Game(config.game.name)
  private var count = 0
  private lateinit var roomExtension: RoomExtension

  init {
    log().info("Initialized")
  }

  override fun supervisorStrategy(): SupervisorStrategy = strategy

  override fun createReceive(): Receive = receiveBuilder()
    .match(StateChanged::class.java) { handle(it) }
    .match(RegisterRoomExtension::class.java) { handle(it) }
    .build()

  /**
   * Receiver method for when CreateMovement message is received
   */
  private fun handle(event: StateChanged) {
    count += event.id
    sender.tell("count=$count", self)
  }

  private fun handle(cmd: RegisterRoomExtension) {
    roomExtension = cmd.extension
  }

  companion object {
    fun props(config: AppConfig): Props = Props.create(GameActor::class.java, config)
  }
}
