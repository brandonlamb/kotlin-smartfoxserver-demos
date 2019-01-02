package com.example.infrastructure.mock

import com.example.domain.core.player.Player
import com.example.domain.core.player.api.PlayerReadRepository
import org.dyn4j.geometry.Vector2
import javax.inject.Singleton

@Singleton
class PlayerReadRepository : PlayerReadRepository {
  override fun findByUsername(username: String): Player? = Player(1, username, Vector2(10.0, 10.0))
}
