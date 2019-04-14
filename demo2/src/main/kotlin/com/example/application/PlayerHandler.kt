package com.example.application

import com.example.domain.core.MovePlayer
import com.example.domain.core.PlayerMoved
import com.example.domain.godot.SceneTree
import com.example.ports.sfs2x.RoomExtension
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener

@Listener
class PlayerHandler(private val tree: SceneTree, private val room: RoomExtension) {
  @Handler
  fun handle(cmd: MovePlayer) {
    val player = tree.root.getNode("players/${cmd.id}")
    val user = room.parentZone.userManager.getUserById(cmd.id)
  }

  @Handler
  fun handle(event: PlayerMoved) {

  }
}
