package com.example.domain.godot

open class Node(val id: NodeID) : Object() {
  var parent: Node? = null

  private var tree: SceneTree? = null
  private val children: MutableMap<String, Node> = mutableMapOf()

  fun physicsProcess(delta: Float) {
  }

  fun process(delta: Float) {
  }

  fun ready() {
  }

  fun addChild(node: Node) {
    children[node.id] = node
    node.setTree(tree)
    node.parent = this
  }

  fun removeChild(node: Node) {
    children.remove(node.id)
    node.setTree(null)
    node.parent = null
  }

  fun getChildCount(): Int = children.size

  fun getChildren(): List<Node> = children.values.toList()

  fun getChild(idx: Int): Node = children.values.elementAt(idx)

  fun getTree(): SceneTree = tree!!

  fun setTree(tree: SceneTree?) {
    this.tree = tree
    children.values.forEach { it.setTree(tree) }
  }

  fun getNode(name: String): Node = children[name] ?: throw Exception("Node does not exist")

  fun propagateReady() {
    children.values.forEach { it.propagateReady() }
    ready()
  }
}
