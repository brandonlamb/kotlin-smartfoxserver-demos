package com.example.infrastructure.auth

import com.example.domain.auth.User
import com.example.domain.auth.api.AuthRepository
import javax.inject.Singleton

@Singleton
class BasicAuthRepository : AuthRepository {
  override fun findUser(username: String): User? =
    if (db.containsKey(username)) User(db[username]!!.id, db[username]!!.username, db[username]!!.password) else null

  private companion object {
    private val db = mapOf(
      "user1" to UserCredential(1, "user1", "password1"),
      "user2" to UserCredential(1, "user2", "password2"),
      "user3" to UserCredential(1, "user3", "password3")
    )
  }
}
