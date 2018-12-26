package com.example.domain.core

import akka.actor.AbstractActorWithTimers
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.Main
import com.example.domain.core.player.AddPlayer
import com.example.domain.core.player.CheckPlayerConnected
import com.example.domain.core.player.Player
import com.example.domain.core.player.api.PlayerReadRepository
import com.example.infrastructure.config.AppConfig
import com.example.ports.sfs2x.RoomExtension
import com.smartfoxserver.v2.entities.User
import com.smartfoxserver.v2.mmo.MMORoom
import com.smartfoxserver.v2.mmo.Vec3D
import org.dyn4j.geometry.Vector2
import org.pmw.tinylog.Logger
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.math.roundToInt

class PlayerActor(
  private val appConfig: AppConfig,
  private val room: RoomExtension,
  private val user: User
) : AbstractActorWithTimers() {
  private object TickKey
  private val playerReadRepository = Main.ctx.getBean(PlayerReadRepository::class.java)
  private var position = Vector2()
  private var health = 0
  private var armor = 0

  init {
    Logger.info("Creating ${context.parent.path()}/${self.path().name()}")
  }

  override fun supervisorStrategy(): SupervisorStrategy = OneForOneStrategy(
    10,
    Duration.create(1, MINUTES),
    DeciderBuilder
      .match(TimeoutException::class.java) { restart() }
      .match(UnstableClientException::class.java) { stop() }
      .build()
  )

  override fun createReceive(): Receive = receiveBuilder()
    .matchAny {
      when (it) {
        is Tick -> handle(it)
        is CheckPlayerConnected -> handle(it)
        is AddPlayer -> handle(it)
      }
    }
    .build()

  /**
   * Main game tick entry point
   *
   * Call whatever needs to be iterated in a server tick here
   */
  private fun handle(cmd: Tick) {
    Logger.info("tick=${context.parent.path()}/${self.path().name()}")

    val mmoRoom = room.parentRoom as MMORoom
    mmoRoom.userList.forEach { user ->
      Logger.info("user={}, position={}", user.name, user)
//      user as User
    }

    context.children.forEach { it.tell(cmd, self) }
  }

  /**
   * Check if the user is connected.
   *
   * If the user is NOT connected, send ourself a PoisonPill to stop this child actor.
   */
  private fun handle(cmd: CheckPlayerConnected) {
    Logger.info("CheckConnected={}", user.name)

    if (!user.isConnected) {
//      self.tell(PoisonPill.getInstance(), self)
      stop()
    }
  }

  /**
   * Add a player
   */
  private fun handle(cmd: AddPlayer) {
    Logger.info("AddPlayer={}", cmd.user.name)

    loadPlayer(cmd.user)

    room.mmoApi.setUserPosition(user, Vec3D(position.x.roundToInt(), position.y.roundToInt(), 0), room.parentRoom)
  }

  private fun loadPlayer(user: User) {
    val player = playerReadRepository.findByUsername(user.name) ?: throw Exception("Not found!")
    loadCurrentHealth(player)
    loadCurrentPosition(player)
  }

  private fun loadCurrentHealth(player: Player) {
    when (player.username) {
      "user1" -> {
        health = 100
        armor = 100
      }
      else -> {
        health = 10
        armor = 10
      }
    }
  }

  private fun loadCurrentPosition(player: Player) {
    position = player.position
  }

  companion object {
    fun props(config: AppConfig, room: RoomExtension, user: User): Props =
      Props.create(PlayerActor::class.java, config, room, user)
  }
}
