package com.example.infra.basic

import com.example.auth.domain.api.AuthRepository
import com.smartfoxserver.v2.exceptions.SFSErrorData
import com.smartfoxserver.v2.exceptions.SFSErrorCode.LOGIN_BAD_PASSWORD
import com.smartfoxserver.v2.exceptions.SFSErrorCode.LOGIN_BAD_USERNAME
import com.smartfoxserver.v2.exceptions.SFSLoginException
import javax.inject.Singleton

@Singleton
class AuthRepo : AuthRepository {
  override fun authenticate(username: String, password: String): Boolean {
    // User does not exist, throw exception
    if (!db.containsKey(username)) {
      val data = SFSErrorData(LOGIN_BAD_USERNAME)
      data.addParameter(username)
      throw SFSLoginException(username, data)
    }

    // Fetch user credentials object from db map
    val user = db[username] ?: throw SFSLoginException(username)

    // Invalid username/password combination
    if (user.password != password) {
      val data = SFSErrorData(LOGIN_BAD_PASSWORD)
      data.addParameter(username)
      throw SFSLoginException(username, data)
    }

    return true
  }

  companion object {
    private val db = mapOf(
      "user1" to UserCredential(1, "user1", "password1"),
      "user2" to UserCredential(1, "user2", "password2"),
      "user3" to UserCredential(1, "user3", "password3")
    )
  }
}
