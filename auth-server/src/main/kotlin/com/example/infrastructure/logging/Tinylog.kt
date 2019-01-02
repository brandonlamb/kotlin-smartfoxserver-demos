package com.example.infrastructure.logging

import org.pmw.tinylog.Configurator
import javax.annotation.PreDestroy

class Tinylog {
  @PreDestroy
  fun preDestroy() {
    Configurator.shutdownWritingThread(true)
  }
}
