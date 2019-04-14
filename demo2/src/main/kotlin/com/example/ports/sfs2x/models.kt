package com.example.ports.sfs2x

object PlayerRequestMappings {
  const val MOVE: Short = 1
  const val ATTACK: Short = 2
  const val CAST_SKILL: Short = 3
}

object ItemRequestMappings {
  const val TRADE: Short = 1
}

enum class PlayerMappings(val value: Short) {
  MOVE(1),
  ATTACK(2),
  CAST_SKILL(3),
  TRADE_ITEM(-10)
}
