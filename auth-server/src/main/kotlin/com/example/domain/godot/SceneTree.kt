package com.example.domain.godot

import org.dyn4j.dynamics.World

open class SceneTree(
  val root: ViewPort = ViewPort(World()),
  private var paused: Boolean = false
) : MainLoop() {
  fun isPaused() = paused

  fun setPaused(value: Boolean) {
    paused = value
  }

  fun getNodeCount() = getCount(root)

  private fun getCount(node: Node): Int {
    var count = 0
    val children = node.getChildren()

    count += children.size

    children.forEach {
      count += getCount(it)
    }

    return count
  }
}
