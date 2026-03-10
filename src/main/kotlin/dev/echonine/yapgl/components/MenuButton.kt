package dev.echonine.yapgl.components

import dev.echonine.yapgl.events.ComponentClickEvent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MenuButton(
    override var id: String = "",
    override var item: ItemStack = ItemStack(Material.AIR),
    override var slots: Set<Int> = emptySet(),
    override var priority: Int = 0,
    var onClick: suspend (ComponentClickEvent) -> Unit = {}
) : MenuComponent() {

    var slot: Int
        get() = slots.firstOrNull() ?: -1
        set(value) { slots = setOf(value) }

    override suspend fun onComponentClick(event: ComponentClickEvent) {
        onClick.invoke(event)
    }
}