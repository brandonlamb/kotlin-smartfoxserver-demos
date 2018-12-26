package com.example.domain.auth

import com.example.domain.auth.api.AuthRepository
import com.example.domain.auth.api.AuthService
import com.smartfoxserver.bitswarm.sessions.ISession
import com.smartfoxserver.v2.api.ISFSApi
import com.smartfoxserver.v2.exceptions.SFSErrorCode.LOGIN_BAD_PASSWORD
import com.smartfoxserver.v2.exceptions.SFSErrorCode.LOGIN_BAD_USERNAME
import com.smartfoxserver.v2.exceptions.SFSErrorData
import com.smartfoxserver.v2.exceptions.SFSLoginException
import javax.inject.Singleton

@Singleton
class AuthService constructor(private val auth: AuthRepository) : AuthService {
  override fun authenticate(api: ISFSApi, session: ISession, username: String, password: String) {
    val user = auth.findUser(username)

    // User does not exist, throw exception
    if (user == null) {
      val data = SFSErrorData(LOGIN_BAD_USERNAME)
      data.addParameter(username)
      throw SFSLoginException(username, data)
    }

    // Invalid username/password combination
    if (!api.checkSecurePassword(session, user.password, password)) {
//    if (user.password != password) {
      val data = SFSErrorData(LOGIN_BAD_PASSWORD)
      data.addParameter(username)
      throw SFSLoginException(username, data)
    }
  }
}
