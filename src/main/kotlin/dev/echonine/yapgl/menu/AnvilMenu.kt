package dev.echonine.yapgl.menu

import dev.echonine.yapgl.components.MenuButton
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Repairable

class AnvilMenu(
    title: Component,
    inputItem: ItemStack = ItemStack(Material.PAPER),
)
    : Menu(title, MenuType.ANVIL) {

    var onSubmit: suspend (player: Player, input: String) -> Unit = { _, _ -> }
    var onInput: suspend (player: Player, input: String) -> Unit = { _, _ -> }

    init {
        val meta = inputItem.itemMeta
        if (meta !is Repairable) {
            throw IllegalArgumentException("inputItem must be a Repairable item, but ${inputItem.type} is not")
        }
        meta.repairCost = 0
        inputItem.itemMeta = meta
        _slots[0] = MenuButton(
            id = "anvil_input",
            item = inputItem,
            slots = setOf(0),
            priority = 1,
        )
    }

    var currentInput: String = ""
        internal set

    internal suspend fun handleSubmit(player: Player, input: String) {
        close(player)
        onSubmit(player, input)
    }

    internal suspend fun updateInput(player: Player, input: String) {
        currentInput = input
        onInput(player, input)
    }
}
