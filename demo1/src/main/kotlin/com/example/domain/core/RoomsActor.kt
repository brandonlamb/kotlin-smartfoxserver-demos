package com.example.domain.core

import akka.actor.AbstractActorWithTimers
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.domain.core.room.CreateRoom
import com.example.domain.core.room.RoomCreated
import com.example.infrastructure.config.AppConfig
import com.example.ports.sfs2x.ZoneExtension
import org.pmw.tinylog.Logger
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.MINUTES

class RoomsActor(private val appConfig: AppConfig, private val zone: ZoneExtension) : AbstractActorWithTimers() {
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
    timers.startPeriodicTimer(TickKey, Tick, java.time.Duration.ofMinutes(1))
  }

  override fun createReceive(): Receive = receiveBuilder()
    .matchAny {
      when (it) {
        is Tick -> handle(it)
        is CreateRoom -> handle(it)
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
  }

  private fun handle(cmd: CreateRoom) {
    Logger.debug("CreateRoom={}", cmd.room.roomId())
    Logger.debug("THREAD={}", Thread.currentThread().name)

    val actorRef = context.actorOf(
      RoomActor.props(appConfig, cmd.room).withDispatcher(appConfig.dispatchers.affinity),
      cmd.room.roomId()
    )
    sender.tell(RoomCreated(actorRef), self)
  }



  companion object {
    fun props(config: AppConfig, zone: ZoneExtension): Props = Props.create(RoomsActor::class.java, config, zone)
  }
}
