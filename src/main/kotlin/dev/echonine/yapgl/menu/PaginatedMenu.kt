package dev.echonine.yapgl.menu

import dev.echonine.yapgl.YAPGL
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

open class PaginatedMenu(
    title: Component,
    type: MenuType
) : Menu(title, type) {

    var page: Int = 0
        protected set

    var contentSlots: List<Int> = emptyList()
    var onPageChange: suspend PaginatedMenu.(page: Int) -> Unit = {}

    private var batchUpdate = false

    override suspend fun refresh() {
        if (batchUpdate) return
        super.refresh()
    }

    open suspend fun setPage(page: Int) = withContext(YAPGL.dispatcher) {
        this@PaginatedMenu.page = page
        for (slot in contentSlots) _slots.remove(slot)
        batchUpdate = true // This feels a bit hacky
        onPageChange(page)
        batchUpdate = false
        if (viewers.isNotEmpty()) refresh()
    }

    suspend fun nextPage() = setPage(page + 1)

    suspend fun previousPage() {
        if (page > 0) setPage(page - 1)
    }

    override suspend fun open(player: Player) {
        if (viewers.isEmpty()) setPage(0)
        super.open(player)
    }
}
