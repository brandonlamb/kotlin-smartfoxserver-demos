package com.example.domain.core

import akka.actor.AbstractActorWithTimers
import akka.actor.ActorRef.noSender
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.resume
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.domain.core.player.AddPlayer
import com.example.domain.core.player.RemovePlayer
import com.example.domain.core.room.CreateRooms
import com.example.domain.core.room.RoomsCreated
import com.example.domain.core.zone.UserDisconnected
import com.example.domain.core.zone.UserJoinedRoom
import com.example.domain.core.zone.UserLeftRoom
import com.example.domain.core.zone.UserLoggedOut
import com.example.infrastructure.config.AppConfig
import com.example.ports.sfs2x.RoomExtension
import com.example.ports.sfs2x.ZoneExtension
import com.smartfoxserver.v2.entities.Room
import com.smartfoxserver.v2.entities.User
import org.pmw.tinylog.Logger
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.MINUTES

class ZoneActor(private val appConfig: AppConfig, private val zone: ZoneExtension) : AbstractActorWithTimers() {
  private object TickKey
  private val users = mutableMapOf<User, MutableSet<Room>>()

  override fun supervisorStrategy(): SupervisorStrategy = OneForOneStrategy(
    10,
    Duration.create(1, MINUTES),
    DeciderBuilder
      .match(TimeoutException::class.java) { restart() }
      .match(UnstableClientException::class.java) { stop() }
      .match(NullPointerException::class.java) { resume() }
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
        is CreateRooms -> handle(it)
        is UserJoinedRoom -> handle(it)
        is UserLeftRoom -> handle(it)
        is UserLoggedOut -> handle(it)
        is UserDisconnected -> handle(it)
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

  private fun handle(cmd: CreateRooms) {
    Logger.info("CreateRooms")

    val actorRef = context.actorOf(
      RoomsActor.props(appConfig, cmd.zone).withDispatcher(appConfig.dispatchers.affinity),
      "rooms"
    )
    sender.tell(RoomsCreated(actorRef), self)
  }

  private fun handle(event: UserJoinedRoom) {
    Logger.info("UserJoinedRoom")

    users[event.user]?.add(event.room) ?: users.put(event.user, mutableSetOf(event.room))

    (event.room.extension as RoomExtension).roomActor().tell(AddPlayer(event.user), noSender())
  }

  private fun handle(event: UserLeftRoom) {
    Logger.info("UserLeftRoom")

    users[event.user]?.remove(event.room)

    (event.room.extension as RoomExtension).roomActor().tell(RemovePlayer(event.user), noSender())
  }

  private fun handle(event: UserLoggedOut) {
    Logger.info("UserLoggedOut")

    users[event.user]?.forEach { room ->
      (room.extension as RoomExtension).roomActor().tell(RemovePlayer(event.user), noSender())
    }
  }

  private fun handle(event: UserDisconnected) {
    Logger.info("UserDisconnected")
    self.tell(UserLoggedOut(event.user), noSender())
  }

  companion object {
    fun props(config: AppConfig, zone: ZoneExtension): Props = Props.create(ZoneActor::class.java, config, zone)
  }
}
