package com.example.domain.godot

data class Velocity(val x: Double = 0.0, val y: Double = 0.0) {
  val speed: Double
    get() = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0))
  val direction: Double
    get() = Math.atan2(y, x)

  fun toComponentsString() = "($x,$y)"
  fun toVectorString() = "[$speed,$direction rad]"
}
