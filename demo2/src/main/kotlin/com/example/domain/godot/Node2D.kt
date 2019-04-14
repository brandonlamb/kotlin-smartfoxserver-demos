package com.example.domain.godot

import org.dyn4j.geometry.Transform
import org.dyn4j.geometry.Vector2

open class Node2D(id: NodeID) : Node(id) {
  var position = Vector2()
  var rotation = 0.0F
  var rotationDegrees = 0.0F
  var scale = Vector2()
  var transform = Transform()
  var globalPosition = Vector2()
  var globalRotation = 0.0F
  var globalRotationDegrees = 0.0F
  var globalScale = Vector2()
}
