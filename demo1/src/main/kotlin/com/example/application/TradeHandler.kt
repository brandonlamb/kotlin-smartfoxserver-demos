package com.example.application

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import com.example.domain.core.trade.ItemTraded
import com.example.domain.core.trade.TradeItem
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener

@Listener
class TradeHandler(private val room: ActorRef) {
  @Handler
  fun handle(cmd: TradeItem) {
    room.tell(cmd, noSender())
  }

  @Handler
  fun handle(evt: ItemTraded) {
    room.tell(evt, noSender())
  }
}
