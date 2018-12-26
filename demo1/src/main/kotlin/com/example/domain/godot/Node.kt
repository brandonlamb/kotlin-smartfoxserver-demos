package com.example.domain.godot

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

open class Node(
  var name: String = "",
  var parent: Node? = null,
  private val children: ConcurrentMap<String, Node> = ConcurrentHashMap()
) : Object() {
  fun physicsProcess(delta: Float) {
  }

  fun process(delta: Float) {
  }

  fun ready() {
  }

  fun addChild(node: Node) {
    children[node.name] = node
  }

  fun removeChild(node: Node) {
    children.remove(node.name)
  }

  fun getChildCount() = children.size

  fun getChildren() = children.values.toList()

  fun getChild(idx: Int) = children.toList()[idx]
}
