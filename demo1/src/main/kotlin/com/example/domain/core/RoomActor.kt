package com.example.domain.core

import akka.actor.AbstractActorWithTimers
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.domain.core.player.AddPlayer
import com.example.domain.core.player.RemovePlayer
import com.example.domain.godot.SceneTree
import com.example.infrastructure.config.AppConfig
import com.example.ports.sfs2x.RoomExtension
import com.smartfoxserver.v2.mmo.IMMOItemVariable
import com.smartfoxserver.v2.mmo.MMOItem
import com.smartfoxserver.v2.mmo.MMOItemVariable
import com.smartfoxserver.v2.mmo.MMORoom
import com.smartfoxserver.v2.mmo.Vec3D
import org.pmw.tinylog.Logger
import java.time.Duration

open class RoomActor(private val appConfig: AppConfig, private val room: RoomExtension) : AbstractActorWithTimers() {
  private object TickKey
  private val tree = SceneTree()
  private val roomId: String by lazy {
    if (room.parentRoom.containsVariable(ROOM_ID)) room.parentRoom.getVariable(ROOM_ID).stringValue else this::class.java.simpleName
  }
  private var tick = 1

  init {
    Logger.info("Creating ${context.parent.path()}/${self.path().name()}")
    timers.startPeriodicTimer(TickKey, Tick, Duration.ofMillis(appConfig.game.tick.toLong()))
  }

  override fun preStart() {
    super.preStart()

    createPlayers()
  }

  override fun supervisorStrategy(): SupervisorStrategy = OneForOneStrategy(
    10,
    Duration.ofMinutes(1),
    DeciderBuilder
      .match(TimeoutException::class.java) { restart() }
      .match(UnstableClientException::class.java) { stop() }
      .build()
  )

  override fun createReceive(): Receive = receiveBuilder()
    .matchAny {
      when (it) {
        is Tick -> handle(it)
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
    Logger.info("tick=${context.parent.path()}/${self.path().name()}")
    context.children.forEach { it.tell(cmd, self) }

    when (room.parentRoom) {
      is MMORoom -> {
        val allItems = (room.parentRoom as MMORoom).allMMOItems
//        allItems.forEach { Logger.info("item={}/{}", room.roomId(), it.variables.first { it.name == "id" }.value) }

        if (allItems.size < 5) {
          val vars = mutableListOf<IMMOItemVariable>()
          vars.add(MMOItemVariable("id", "item-$tick"))
          room.mmoApi.setMMOItemPosition(MMOItem(vars), Vec3D(1, 1, 0), room.parentRoom as MMORoom)
        }
      }
    }

    tick++
  }

  private fun handle(cmd: AddPlayer) {
    Logger.info("AddPlayer={}", cmd.user.name)

    context.child("players").get().tell(cmd, self)
  }

  private fun handle(cmd: RemovePlayer) {
    Logger.info("RemovePlayer={}", cmd.user.name)

    context.child("players").get().tell(cmd, self)
  }

  private fun createPlayers() {
    context.actorOf(
      PlayersActor.props(appConfig, room).withDispatcher(appConfig.dispatchers.affinity),
      PLAYERS
    )
  }

  companion object {
    private const val ROOM_ID = "roomId"
    private const val PLAYERS = "players"

    fun props(config: AppConfig, room: RoomExtension): Props = Props.create(RoomActor::class.java, config, room)
  }
}
