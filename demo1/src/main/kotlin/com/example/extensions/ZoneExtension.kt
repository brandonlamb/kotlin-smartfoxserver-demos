package com.example.extensions

import com.example.Main
import com.example.auth.domain.api.AuthRepository
import com.example.infra.basic.AuthRepo
import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
import com.smartfoxserver.v2.extensions.SFSExtension
import javax.enterprise.inject.se.SeContainer
import javax.enterprise.inject.se.SeContainerInitializer

@Instantiation(SINGLE_INSTANCE)
class ZoneExtension : SFSExtension() {
  lateinit var cdi: SeContainer

  private lateinit var authRepo: AuthRepository

  override fun init() {
    try {
      cdi = SeContainerInitializer
        .newInstance()
//      .addPackages(true, Main.javaClass.`package`)
        .addBeanClasses(AuthRepo::class.java)
        .initialize()

      authRepo = cdi.beanManager.createInstance().select(AuthRepository::class.java).get()
      val x = authRepo.authenticate("test", "test")
      trace("authed")
      println(x)
    } catch (e: Exception) {
      trace(e.message)
    }

    trace("Loaded from CDI")
  }
}
