package com.example

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.Micronaut

object Main {
  val ctx: ApplicationContext = Micronaut.build().packages("com.example").mainClass(Main::class.java).start()

  init {
    println("Initializing game extension")
  }

  @JvmStatic
  fun main(args: Array<String>) {
  }
}
