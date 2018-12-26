package com.example.domain.core

import akka.actor.AbstractActorWithTimers
import akka.actor.OneForOneStrategy
import akka.actor.PoisonPill
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.domain.core.player.AddPlayer
import com.example.domain.core.player.CheckPlayerConnected
import com.example.domain.core.player.RemovePlayer
import com.example.infrastructure.config.AppConfig
import com.example.ports.sfs2x.RoomExtension
import org.pmw.tinylog.Logger
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.MINUTES

class PlayersActor(private val appConfig: AppConfig, private val room: RoomExtension) : AbstractActorWithTimers() {
  private object TickKey

  override fun supervisorStrategy(): SupervisorStrategy = OneForOneStrategy(
    10,
    Duration.create(1, MINUTES),
    DeciderBuilder
      .match(TimeoutException::class.java) { restart() }
      .match(UnstableClientException::class.java) { stop() }
      .build()
  )

  init {
    Logger.info("Creating ${context.parent.path()}/${self.path().name()}")
    timers.startPeriodicTimer(TickKey, CheckPlayerConnected, java.time.Duration.ofSeconds(CHECK_CONNECTED_TIMEOUT))
  }

  override fun createReceive(): Receive = receiveBuilder()
    .matchAny {
      when (it) {
        is Tick -> handle(it)
        is CheckPlayerConnected -> handle(it)
        is AddPlayer -> handle(it)
        is RemovePlayer -> handle(it)
      }
    }
    .build()

  /**
   * Main game tick entry point
   *
   * Call whatever needs to be iterated in a server tick here
   */
  private fun handle(cmd: Tick) {
    Logger.debug("tick=${context.parent.path()}/${self.path().name()}")
    context.children.forEach { it.tell(cmd, self) }
  }

  /**
   * Periodically tell child players to check if the user is connected
   */
  private fun handle(cmd: CheckPlayerConnected) {
    Logger.debug("CheckConnected")

    context.children.forEach {
      it.tell(cmd, self)
    }
  }

  /**
   * Add a child player
   */
  private fun handle(cmd: AddPlayer) {
    Logger.info("AddPlayer={}", cmd.user.name)

    context.actorOf(
      PlayerActor.props(appConfig, room, cmd.user).withDispatcher(appConfig.dispatchers.affinity),
      "${cmd.user.id}"
    )
  }

  /**
   * Remove a child player
   */
  private fun handle(cmd: RemovePlayer) {
    Logger.info("RemovePlayer={}", cmd.user.name)

    context.child("${cmd.user.id}").get().tell(PoisonPill.getInstance(), self)
  }

  companion object {
    private const val CHECK_CONNECTED_TIMEOUT = 10L

    fun props(config: AppConfig, room: RoomExtension): Props = Props.create(PlayersActor::class.java, config, room)
  }
}
