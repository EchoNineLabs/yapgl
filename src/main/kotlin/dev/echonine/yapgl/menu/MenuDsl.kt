package dev.echonine.yapgl.menu

import dev.echonine.yapgl.YAPGL
import dev.echonine.yapgl.components.AnimatedButton
import dev.echonine.yapgl.components.ListButton
import dev.echonine.yapgl.components.MenuButton
import dev.echonine.yapgl.events.ComponentClickEvent
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

suspend fun menu(title: Component, type: MenuType, block: suspend Menu.() -> Unit): Menu =
    withContext(YAPGL.dispatcher) { Menu(title, type).also { it.block() } }

suspend fun Menu.button(block: MenuButton.() -> Unit) {
    val btn = MenuButton().apply(block)
    if (btn.id.isEmpty()) btn.id = "${btn.item.type.name.lowercase()}_${btn.slots.hashCode()}"
    addComponent(btn)
}

suspend fun Menu.button(item: ItemStack, slot: Int, onClick: suspend (ComponentClickEvent) -> Unit) {
    addComponent(MenuButton(
        id = "${item.type.name.lowercase()}_$slot",
        item = item,
        slots = setOf(slot),
        onClick = onClick
    ))
}


fun anvilMenu(title: Component, inputItem: ItemStack = ItemStack(Material.PAPER), block: AnvilMenu.() -> Unit): AnvilMenu =
    AnvilMenu(title, inputItem).apply(block)

suspend fun paginatedMenu(title: Component, type: MenuType, block: suspend PaginatedMenu.() -> Unit): PaginatedMenu =
    withContext(YAPGL.dispatcher) { PaginatedMenu(title, type).also { it.block() } }

suspend fun <T> autoPaginatedMenu(title: Component, type: MenuType, items: List<T>, block: suspend AutoPaginatedMenu<T>.() -> Unit): AutoPaginatedMenu<T> =
    withContext(YAPGL.dispatcher) { AutoPaginatedMenu<T>(title, type).also { it.items = items; it.block() } }

suspend fun <T> Menu.listButton(options: List<T>, block: ListButton<T>.() -> Unit) {
    val btn = ListButton<T>(options = options).apply(block)
    btn.updateLore()
    if (btn.id.isEmpty()) btn.id = "list_${btn.slots.hashCode()}"
    addComponent(btn)
}

suspend fun Menu.toggleButton(block: ListButton<Boolean>.() -> Unit) {
    val btn = ListButton(
        options = listOf(true, false),
        displayName = { if (it) "Enabled" else "Disabled" },
    ).apply(block)
    btn.updateLore()
    if (btn.id.isEmpty()) btn.id = "toggle_${btn.slots.hashCode()}"
    addComponent(btn)
}

suspend fun Menu.animatedButton(block: AnimatedButton.() -> Unit) {
    val btn = AnimatedButton().apply(block)
    if (btn.id.isEmpty()) btn.id = "animated_${btn.slots.hashCode()}"
    if (btn.frames.isNotEmpty()) btn.item = btn.frames[0]
    addComponent(btn)
    btn.start(this)
}
