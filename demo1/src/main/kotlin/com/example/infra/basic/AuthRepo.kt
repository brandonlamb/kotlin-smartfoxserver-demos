package com.example.infra.basic

import com.example.auth.domain.api.AuthRepository
import javax.inject.Singleton

@Singleton
open class AuthRepo : AuthRepository {
  override fun authenticate(username: String, password: String): Boolean {
    return true
  }
}
