package com.example.domain.core.player

import com.example.domain.godot.Node2D
import com.example.domain.godot.NodeID

open class Player(id: NodeID) : Node2D(id) {
  private val state = PlayerState()

  private data class PlayerState(
    var health: Int = 0
  )
}
