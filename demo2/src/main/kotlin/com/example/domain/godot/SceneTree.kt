package com.example.domain.godot

import com.example.ports.sfs2x.RoomExtension
import org.dyn4j.dynamics.World

open class SceneTree(
  private val room: RoomExtension,
  private val eventBus: (Any) -> Unit,
  val root: ViewPort = ViewPort("viewport", World())
) : MainLoop() {
  private var paused = false

  fun isPaused(): Boolean = paused

  fun setPaused(value: Boolean) {
    paused = value
  }

  fun getNodeCount(): Int = getCount(root)

  private fun getCount(node: Node): Int {
    var count = 0
    val children = node.getChildren()

    count += children.size

    children.forEach {
      count += getCount(it)
    }

    return count
  }

  /**
   * Emit a message (which could be a command, event, other), and delegate to a message bus
   */
  fun emit(message: Any) {
    eventBus.invoke(message)
  }
}
