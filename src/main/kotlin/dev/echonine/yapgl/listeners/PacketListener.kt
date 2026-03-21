package dev.echonine.yapgl.listeners

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem
import dev.echonine.yapgl.menu.AnvilMenu
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import dev.echonine.yapgl.YAPGL
import dev.echonine.yapgl.events.ComponentClickEvent
import dev.echonine.yapgl.extenstions.sendPacket
import dev.echonine.yapgl.extenstions.toBukkitClickType
import dev.echonine.yapgl.menu.MenuRegistry
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private const val CLICK_COOLDOWN_MS = 150L

class PacketListener : PacketListenerAbstract() {

    private val lastClickTime = ConcurrentHashMap<UUID, Long>()

    override fun onPacketReceive(event: PacketReceiveEvent) {

        when (event.packetType) {
            PacketType.Play.Client.CLOSE_WINDOW -> {
                val player = event.getPlayer<Player>()
                val windowId = WrapperPlayClientCloseWindow(event).windowId
                if (windowId != YAPGL.CONTAINER_ID) return
                YAPGL.scope.launch {
                    val menu = MenuRegistry.getMenu(player) ?: return@launch
                    MenuRegistry.close(player)
                    menu.viewers.remove(player)
                    if (menu.hasInventoryComponents) menu.restorePlayerInventory(player)
                }
            }

            PacketType.Play.Client.CLICK_WINDOW -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayClientClickWindow(event)
                if (packet.windowId != YAPGL.CONTAINER_ID) return
                val slot = packet.slot
                val clickType = packet.windowClickType.toBukkitClickType()
                val isPickupAll = packet.windowClickType == WrapperPlayClientClickWindow.WindowClickType.PICKUP_ALL
                val isQuickMove = packet.windowClickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE
                val isSwap = packet.windowClickType == WrapperPlayClientClickWindow.WindowClickType.SWAP
                val swapButton = if (isSwap) packet.button else -1

                event.isCancelled = true

                if (slot < 0) return

                val now = System.currentTimeMillis()
                val last = lastClickTime.put(player.uniqueId, now)
                val cooldownRejected = last != null && now - last < CLICK_COOLDOWN_MS

                YAPGL.scope.launch {
                    val menu = MenuRegistry.getMenu(player) ?: return@launch

                    if (menu is AnvilMenu && slot == 2) {
                        if (!cooldownRejected) menu.handleSubmit(player, menu.currentInput)
                        return@launch
                    }

                    if (isQuickMove) {
                        menu.fullRefresh(player)
                        return@launch
                    }

                    val component = menu.getComponentAtSlot(slot)

                    player.sendPacket(
                        WrapperPlayServerSetSlot(
                            -1, 0, -1,
                            SpigotConversionUtil.fromBukkitItemStack(ItemStack(Material.AIR))
                        )
                    )

                    if (isPickupAll) {
                        menu.fullRefresh(player)
                    } else if (isSwap) {
                        val correctionItem = component?.item ?: ItemStack(Material.AIR)
                        player.sendPacket(WrapperPlayServerSetSlot(
                            YAPGL.CONTAINER_ID, 0, slot,
                            SpigotConversionUtil.fromBukkitItemStack(correctionItem)
                        ))
                        if (swapButton == 40) {
                            player.sendPacket(WrapperPlayServerSetSlot(
                                0, 0, 45,
                                SpigotConversionUtil.fromBukkitItemStack(player.inventory.itemInOffHand)
                            ))
                        } else {
                            val hotbarPacketSlot = menu.type.size + 27 + swapButton
                            val hotbarItem = menu.slots[hotbarPacketSlot]?.item
                                ?: menu.inventoryItemAt(player, hotbarPacketSlot)
                            player.sendPacket(WrapperPlayServerSetSlot(
                                YAPGL.CONTAINER_ID, 0, hotbarPacketSlot,
                                SpigotConversionUtil.fromBukkitItemStack(hotbarItem)
                            ))
                        }
                    } else {
                        val correctionItem = component?.item
                            ?: if (slot >= menu.type.size) menu.inventoryItemAt(player, slot)
                            else ItemStack(Material.AIR)
                        player.sendPacket(
                            WrapperPlayServerSetSlot(
                                YAPGL.CONTAINER_ID, 0, slot,
                                SpigotConversionUtil.fromBukkitItemStack(correctionItem)
                            )
                        )
                    }

                    if (cooldownRejected || component == null) return@launch

                    YAPGL.scope.launch(Dispatchers.Default) {
                        component.onComponentClick(
                            ComponentClickEvent(
                                player = player,
                                slot = slot,
                                clickType = clickType,
                                menu = menu
                            )
                        )
                    }
                }
            }

            PacketType.Play.Client.NAME_ITEM -> {
                val player = event.getPlayer<Player>()
                val input = WrapperPlayClientNameItem(event).itemName
                YAPGL.scope.launch {
                    (MenuRegistry.getMenu(player) as? AnvilMenu)?.updateInput(player, input)
                }
            }

        }
    }
}

