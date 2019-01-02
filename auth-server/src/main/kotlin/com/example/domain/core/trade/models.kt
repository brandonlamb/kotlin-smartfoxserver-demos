package com.example.domain.core.trade

// Commands
sealed class Command

data class TradeItem(val id: Int) : Command()

// Events
sealed class Event

data class ItemTraded(val id: Int) : Event()
