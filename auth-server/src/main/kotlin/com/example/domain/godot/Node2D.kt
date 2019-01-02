package com.example.domain.godot

import org.dyn4j.geometry.Transform
import org.dyn4j.geometry.Vector2

open class Node2D(
  var position: Vector2 = Vector2(),
  var rotation: Float = 0.0F,
  var rotationDegrees: Float = 0.0F,
  var scale: Vector2 = Vector2(),
  var transform: Transform = Transform(),
  var globalPosition: Vector2 = Vector2(),
  var globalRotation: Float = 0.0F,
  var globalRotationDegrees: Float = 0.0F,
  var globalScale: Vector2 = Vector2()
) : Node()
