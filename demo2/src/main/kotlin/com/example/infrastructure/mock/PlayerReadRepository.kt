package com.example.infrastructure.mock

import com.example.domain.core.player.Player
import com.example.domain.core.player.api.PlayerReadRepository
import javax.inject.Singleton

@Singleton
class PlayerReadRepository : PlayerReadRepository {
  override fun findByUsername(username: String): Player? = null
}
