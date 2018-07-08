package com.example.extensions

import com.example.auth.domain.api.AuthRepository
import com.example.infra.basic.AuthRepo
import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
import com.smartfoxserver.v2.core.SFSEventParam.LOGIN_NAME
import com.smartfoxserver.v2.core.SFSEventParam.LOGIN_PASSWORD
import com.smartfoxserver.v2.core.SFSEventType.SERVER_READY
import com.smartfoxserver.v2.core.SFSEventType.USER_LOGIN
import com.smartfoxserver.v2.extensions.SFSExtension
import javax.enterprise.inject.se.SeContainer
import javax.enterprise.inject.se.SeContainerInitializer

@Instantiation(SINGLE_INSTANCE)
class ZoneExtension : SFSExtension() {
  lateinit var cdi: SeContainer
    private set

  private lateinit var authRepo: AuthRepository

  override fun init() {
    try {
      cdi = SeContainerInitializer
        .newInstance()
        .addBeanClasses(AuthRepo::class.java)
        .initialize()

      authRepo = cdi.beanManager.createInstance().select(AuthRepository::class.java).get()

      addEventListener(SERVER_READY) { trace("Server ready") }

      addEventListener(USER_LOGIN) {
        val username: String = it.getParameter(LOGIN_NAME) as String
        val password: String = it.getParameter(LOGIN_PASSWORD) as String
        authRepo.authenticate(username, password)
        trace("User successfully authenticated")
      }
    } catch (e: Exception) {
      trace("ERROR: ${e.message}")
    }

    trace("Loaded ZoneExtension")
  }
}
