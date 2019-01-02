package com.example.application

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import com.example.domain.core.player.MovePlayer
import com.example.domain.core.player.PlayerMoved
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener

@Listener
class PlayerHandler(private val room: ActorRef) {
  @Handler
  fun handle(cmd: MovePlayer) {
    room.tell(cmd, noSender())
  }

  @Handler
  fun handle(evt: PlayerMoved) {
    room.tell(evt, noSender())
  }
}
