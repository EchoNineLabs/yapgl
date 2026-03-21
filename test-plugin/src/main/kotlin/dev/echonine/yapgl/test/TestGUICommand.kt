package dev.echonine.yapgl.test

import dev.echonine.yapgl.components.MenuButton
import dev.echonine.yapgl.menu.MenuType
import dev.echonine.yapgl.menu.anvilMenu
import dev.echonine.yapgl.menu.autoPaginatedMenu
import dev.echonine.yapgl.menu.button
import dev.echonine.yapgl.menu.animatedButton
import dev.echonine.yapgl.menu.listButton
import dev.echonine.yapgl.menu.menu
import dev.echonine.yapgl.menu.paginatedMenu
import dev.echonine.yapgl.menu.toggleButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestGUICommand : Command("testyapgl") {
    // Normally you would use something like MCCoroutine to run suspend functions from commands,
    // but for simplicity we use a plain CoroutineScope here.
    val scope = CoroutineScope(Dispatchers.Default)
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return false
        scope.launch {
            val menu = menu(Component.text("Test Menu"), MenuType.CHEST_6_ROWS) {
                button(ItemStack(Material.DIAMOND), 0) {
                    it.player.sendRichMessage("You clicked the diamond button! ${it.clickType}")
                }

                button(ItemStack(Material.GOLD_INGOT), 1) {
                    close(it.player)
                }

                button(ItemStack(Material.NAME_TAG), 2) { event ->
                    anvilMenu(Component.text("Enter a new title")) {
                        onSubmit = { p, input ->
                            setTitle(MiniMessage.miniMessage().deserialize(input))
                            open(p)
                        }
                    }.open(event.player)
                }

                // Manual pagination
                button(ItemStack(Material.COMPASS), 3) { event ->
                    val allMaterials = Material.entries.filter { it.isItem }
                    val randomItems = (1..100).map { allMaterials.random() }

                    paginatedMenu(Component.text("Random Items"), MenuType.CHEST_6_ROWS) {
                        contentSlots = (10..16) + (19..25) + (28..34) + (37..43)
                        onPageChange = { page ->
                            val pageItems = randomItems.drop(page * contentSlots.size).take(contentSlots.size)
                            pageItems.forEachIndexed { i, mat ->
                                addComponent(MenuButton(
                                    item = ItemStack(mat),
                                    slots = setOf(contentSlots[i]),
                                ))
                            }
                        }
                        button(ItemStack(Material.ARROW), 48) { previousPage() }
                        button(ItemStack(Material.ARROW), 50) { nextPage() }
                        fill(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                    }.open(event.player)
                }

                // Auto pagination
                button(ItemStack(Material.BOOK), 4) { event ->
                    val materials = Material.entries.filter { it.isItem }
                    autoPaginatedMenu(Component.text("Material Selector"), MenuType.CHEST_6_ROWS, materials) {
                        contentSlots = (10..16) + (19..25) + (28..34) + (37..43)
                        renderItem = { material ->
                            MenuButton(
                                item = ItemStack(material),
                                onClick = { e ->
                                    close(e.player)
                                    e.player.sendRichMessage("You selected: <gold>${material.name}</gold>")
                                }
                            )
                        }
                        button(ItemStack(Material.ARROW), 48) { previousPage() }
                        button(ItemStack(Material.ARROW), 50) { nextPage() }
                        fill(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                    }.open(event.player)
                }

                // List button — difficulty selector
                listButton(listOf("Easy", "Normal", "Hard", "Nightmare")) {
                    item = ItemStack(Material.IRON_SWORD)
                    slot = 5
                    displayName = { it }
                    selectedFormat = "<green> > <option>"
                    unselectedFormat = "<gray>   <option>"
                    onOptionChange = { event, option ->
                        event.player.sendRichMessage("Difficulty set to: <yellow>$option")
                    }
                }

                // Toggle button
                toggleButton {
                    item = ItemStack(Material.REDSTONE_TORCH)
                    slot = 6
                    onOptionChange = { event, enabled ->
                        event.player.sendRichMessage("PvP is now: <yellow>${if (enabled) "ON" else "OFF"}")
                    }
                }

                // Animated button — rainbow cycle
                animatedButton {
                    slot = 7
                    intervalTicks = 5
                    frames = listOf(
                        ItemStack(Material.RED_STAINED_GLASS_PANE),
                        ItemStack(Material.ORANGE_STAINED_GLASS_PANE),
                        ItemStack(Material.YELLOW_STAINED_GLASS_PANE),
                        ItemStack(Material.LIME_STAINED_GLASS_PANE),
                        ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
                        ItemStack(Material.PURPLE_STAINED_GLASS_PANE),
                    )
                    onClick = { it.player.sendRichMessage("<rainbow>Rainbow!") }
                }

                // Example of using components in the players inventory
                button(ItemStack(Material.CLOCK), 8) { event ->
                    var count = 0

                    fun counterItem() = ItemStack(Material.LIME_DYE).apply {
                        itemMeta = itemMeta?.also { it.displayName(Component.text(count.toString())) }
                    }

                    menu(Component.text("Counter"), MenuType.CHEST_1_ROW) {
                        // Center display
                        button {
                            id = "counter"
                            item = counterItem()
                            slot = 4
                            priority = 1
                        }

                        button {
                            id = "decrease"
                            item = ItemStack(Material.RED_CONCRETE).apply {
                                itemMeta = itemMeta?.also { it.displayName(Component.text("-")) }
                            }
                            slot = 39
                            priority = 1
                            onClick = {
                                count--
                                (getComponentAtSlot(4) as? MenuButton)?.item = counterItem()
                                refresh()
                            }
                        }

                        button {
                            id = "increase"
                            item = ItemStack(Material.LIME_CONCRETE).apply {
                                itemMeta = itemMeta?.also { it.displayName(Component.text("+")) }
                            }
                            slot = 41
                            priority = 1
                            onClick = {
                                count++
                                (getComponentAtSlot(4) as? MenuButton)?.item = counterItem()
                                refresh()
                            }
                        }

                        fill(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                    }.open(event.player)
                }

                fill(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
            }
            menu.open(player)
        }
        return true
    }
}
