package dev.echonine.yapgl.menu

import dev.echonine.yapgl.YAPGL
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.util.UUID

object MenuRegistry {
        private val openMenus = mutableMapOf<UUID, Menu>()

        suspend fun open(player: Player, menu: Menu) = withContext(YAPGL.dispatcher) {
            openMenus[player.uniqueId] = menu
        }

        suspend fun close(player: Player) = withContext(YAPGL.dispatcher) {
            openMenus.remove(player.uniqueId)
        }

        suspend fun getMenu(player: Player): Menu? = withContext(YAPGL.dispatcher) {
            openMenus[player.uniqueId]
        }
}