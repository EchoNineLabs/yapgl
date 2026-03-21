package dev.echonine.yapgl.menu
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import dev.echonine.yapgl.YAPGL
import dev.echonine.yapgl.components.MenuButton
import dev.echonine.yapgl.components.MenuComponent
import dev.echonine.yapgl.extenstions.sendPacket
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class Menu(
    var title: Component,
    val type: MenuType
) {
    protected val _slots = mutableMapOf<Int, MenuComponent>()
    val slots: Map<Int, MenuComponent> get() = _slots
    val viewers = mutableSetOf<Player>()
    internal var building = false

    open suspend fun addComponent(vararg component: MenuComponent) {
        if (building) {
            putComponents(*component)
            return
        }
        withContext(YAPGL.dispatcher) {
            putComponents(*component)
            if (viewers.isNotEmpty()) refresh()
        }
    }

    private fun putComponents(vararg component: MenuComponent) {
        component.sortedByDescending { it.priority }.forEach { c ->
            c.slots.forEach { s ->
                val existing = _slots[s]
                if (existing == null || existing.priority < c.priority) _slots[s] = c
            }
        }
    }

    open suspend fun removeComponent(component: MenuComponent) = withContext(YAPGL.dispatcher) {
        for (s in component.slots) {
            if (_slots[s] == component) _slots.remove(s)
        }
        if (viewers.isNotEmpty()) refresh()
    }

    open suspend fun getComponentAtSlot(slot: Int): MenuComponent? = withContext(YAPGL.dispatcher) {
        _slots[slot]
    }

    /** Fill all unoccupied slots with [item]. */
    open suspend fun fill(item: ItemStack) = withContext(YAPGL.dispatcher) {
        for (slot in 0 until type.size) {
            if (!_slots.containsKey(slot)) {
                _slots[slot] = MenuButton(
                    id = "filler_$slot",
                    item = item,
                    slots = setOf(slot),
                    priority = 0,
                )
            }
        }
        if (viewers.isNotEmpty()) refresh()
    }

    val hasInventoryComponents: Boolean get() = _slots.keys.any { it >= type.size }

    internal fun inventoryItemAt(player: Player, packetSlot: Int): ItemStack {
        val offset = packetSlot - type.size
        val invSlot = if (offset < 27) offset + 9 else offset - 27
        return player.inventory.getItem(invSlot) ?: ItemStack(Material.AIR)
    }

    internal suspend fun fullRefresh(player: Player) = withContext(YAPGL.dispatcher) {
        val containerItems = (0 until type.size).map { slot ->
            SpigotConversionUtil.fromBukkitItemStack(_slots[slot]?.item ?: ItemStack(Material.AIR))
        }
        val playerInvItems = (0..35).map { offset ->
            val packetSlot = type.size + offset
            val item = _slots[packetSlot]?.item ?: if (hasInventoryComponents) ItemStack(Material.AIR)
                        else inventoryItemAt(player, packetSlot)
            SpigotConversionUtil.fromBukkitItemStack(item)
        }
        player.sendPacket(WrapperPlayServerWindowItems(YAPGL.CONTAINER_ID, 0, containerItems + playerInvItems, null))
    }

    internal suspend fun restorePlayerInventory(player: Player) = withContext(YAPGL.dispatcher) {
        for (offset in 0..35) {
            val packetSlot = type.size + offset
            val item = inventoryItemAt(player, packetSlot)
            val bukkitSlot = if (offset < 27) offset + 9 else offset - 27
            val window0Slot = if (bukkitSlot >= 9) bukkitSlot else 36 + bukkitSlot
            player.sendPacket(WrapperPlayServerSetSlot(0, 0, window0Slot,
                SpigotConversionUtil.fromBukkitItemStack(item)))
        }
    }

    open suspend fun open(player: Player) = withContext(YAPGL.dispatcher) {
        player.sendPacket(WrapperPlayServerOpenWindow(YAPGL.CONTAINER_ID, type.id, title))

        MenuRegistry.open(player, this@Menu)

        viewers.add(player)

        if (hasInventoryComponents) fullRefresh(player) else refresh()
    }

    open suspend fun close(player: Player) = withContext(YAPGL.dispatcher) {
        player.sendPacket(WrapperPlayServerCloseWindow(YAPGL.CONTAINER_ID))

        MenuRegistry.close(player)

        viewers.remove(player)
    }

    open suspend fun refresh() = withContext(YAPGL.dispatcher) {
        val items = (0 until type.size).map { slot ->
            SpigotConversionUtil.fromBukkitItemStack(_slots[slot]?.item)
                ?: SpigotConversionUtil.fromBukkitItemStack(ItemStack(Material.AIR))
        }
        val packet = WrapperPlayServerWindowItems(YAPGL.CONTAINER_ID, 0, items, null)
        viewers.forEach { it.sendPacket(packet) }
    }

    open suspend fun setTitle(newTitle: Component) = withContext(YAPGL.dispatcher) {
        title = newTitle
        viewers.forEach {
            it.sendPacket(WrapperPlayServerOpenWindow(YAPGL.CONTAINER_ID, type.id, newTitle))
        }
    }
}
