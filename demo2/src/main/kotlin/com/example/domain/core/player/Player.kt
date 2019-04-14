package com.example.domain.core.player

import com.example.domain.core.MovePlayer
import com.example.domain.godot.Node2D
import com.example.domain.godot.NodeId

open class Player(id: NodeId, playerId: PlayerId) : Node2D(id) {
  private val state = State()

  override fun process(delta: Float) {
    emit(MovePlayer(123, position))
  }

  fun load(state: State) {
    this.state.apply {
      health = state.health
      shields = state.shields
      energy = state.energy
    }
  }

  data class State(
    var health: Int = 0,
    var shields: Int = 0,
    var energy: Int = 0
  )
}
