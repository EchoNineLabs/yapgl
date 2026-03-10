package dev.echonine.yapgl.events

import dev.echonine.yapgl.menu.Menu
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class ComponentClickEvent(
    val player: Player,
    val slot: Int,
    val clickType: ClickType,
    val menu: Menu
)