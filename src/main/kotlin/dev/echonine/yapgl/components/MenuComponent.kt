package dev.echonine.yapgl.components

import dev.echonine.yapgl.events.ComponentClickEvent
import org.bukkit.inventory.ItemStack


abstract class MenuComponent {
    abstract val id: String
    abstract val item: ItemStack
    abstract val slots: Set<Int>
    abstract val priority: Int
    abstract suspend fun onComponentClick(event: ComponentClickEvent)
}