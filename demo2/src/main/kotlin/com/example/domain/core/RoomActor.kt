package com.example.domain.core

import akka.actor.AbstractActorWithTimers
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.domain.core.player.Player
import com.example.domain.godot.Node
import com.example.domain.godot.SceneTree
import com.example.infrastructure.config.AppConfig
import com.example.ports.sfs2x.RoomExtension
import net.engio.mbassy.bus.MBassador
import org.pmw.tinylog.Logger
import java.lang.System.currentTimeMillis
import java.time.Duration

open class RoomActor(appConfig: AppConfig, room: RoomExtension) : AbstractActorWithTimers() {
  private object Tick
  private object TickKey

  private val eventBus = MBassador<Any>()
  private val tree: SceneTree

  private var tick = 1L
  private var lastTick = currentTimeMillis()

  init {
    Logger.info("Creating ${context.parent.path()}/${self.path().name()}")

    tree = SceneTree(room, { eventBus.publish(it) })
    timers.startPeriodicTimer(TickKey, Tick, Duration.ofMillis(appConfig.game.tick.toLong()))

    createWorld()
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
        is Command -> tree.emit(it)
        is Event -> tree.emit(it)
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

    // Determine delta between last tick
    val now = currentTimeMillis()
    val diff = now.minus(lastTick)
    lastTick = now

    // Process scene tree
    tree.root.process(diff.toFloat())

    tick++
  }

  private fun createWorld() {
    val players = Node("players")
    val npcs = Node("npcs")

    tree.root.addChild(players)
    tree.root.addChild(npcs)

    players.addChild(Player("1001", 1001))
    players.addChild(Player("1002", 1002))

    val p2 = tree.root.getNode("players").getNode("1002")
  }

  companion object {
    fun props(config: AppConfig, room: RoomExtension): Props = Props.create(RoomActor::class.java, config, room)
  }
}
