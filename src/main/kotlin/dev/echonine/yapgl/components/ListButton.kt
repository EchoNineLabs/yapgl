package dev.echonine.yapgl.components

import dev.echonine.yapgl.events.ComponentClickEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ListButton<T>(
    override var id: String = "",
    override var item: ItemStack = ItemStack(Material.STONE),
    override var slots: Set<Int> = emptySet(),
    override var priority: Int = 0,
    var options: List<T> = emptyList(),
    var displayName: (T) -> String = { it.toString() },
    var selectedFormat: String = "<green> > <option>",
    var unselectedFormat: String = "<gray><option>",
    var selectedIndex: Int = 0,
    var onOptionChange: suspend (ComponentClickEvent, T) -> Unit = { _, _ -> }
) : MenuComponent() {

    var slot: Int
        get() = slots.firstOrNull() ?: -1
        set(value) { slots = setOf(value) }

    val selectedOption: T? get() = options.getOrNull(selectedIndex)

    fun updateLore() {
        if (options.isEmpty()) return
        val meta = item.itemMeta ?: return
        meta.lore(options.mapIndexed { index, option ->
            val format = if (index == selectedIndex) selectedFormat else unselectedFormat
            val resolver = TagResolver.resolver("option", Tag.preProcessParsed(displayName(option)))
            MiniMessage.miniMessage().deserialize(format, resolver)
        })
        item.itemMeta = meta
    }

    override suspend fun onComponentClick(event: ComponentClickEvent) {
        if (options.isEmpty()) return
        selectedIndex = (selectedIndex + 1) % options.size
        updateLore()
        event.menu.refresh()
        selectedOption?.let { onOptionChange(event, it) }
    }
}
