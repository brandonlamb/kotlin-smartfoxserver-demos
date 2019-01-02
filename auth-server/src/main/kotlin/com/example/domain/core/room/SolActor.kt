package com.example.domain.core.room

import akka.actor.Props
import com.example.domain.core.RoomActor
import com.example.domain.core.SayHello
import com.example.domain.core.Tick
import com.example.domain.core.player.AddPlayer
import com.example.domain.godot.SceneTree
import com.example.infrastructure.config.AppConfig
import org.pmw.tinylog.Logger

class SolActor(private val appConfig: AppConfig, private val room: RoomExtension) : RoomActor(appConfig, room) {
  private val tree = SceneTree()

  override fun createReceive(): Receive = receiveBuilder()
    .matchAny {
      when (it) {
        is Tick -> handle(it)
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
    Logger.info("tick={}", room.name)
    context.children.forEach { it.tell(cmd, self) }
  }

  private fun handle(cmd: AddPlayer) {

  }

  private fun handle(msg: SayHello) {
    msg.message.trim()
  }

  companion object {
    fun props(config: AppConfig, room: RoomExtension): Props = Props.create(SolActor::class.java, config, room)
  }
}
