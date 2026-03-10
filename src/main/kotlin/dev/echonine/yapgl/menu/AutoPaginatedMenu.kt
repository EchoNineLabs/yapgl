package dev.echonine.yapgl.menu

import dev.echonine.yapgl.YAPGL
import dev.echonine.yapgl.components.MenuButton
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import kotlin.math.ceil

class AutoPaginatedMenu<T>(
    title: Component,
    type: MenuType,
) : PaginatedMenu(title, type) {

    var items: List<T> = emptyList()
    var renderItem: (T) -> MenuButton = { MenuButton() }

    val pageSize: Int get() = contentSlots.size
    val totalPages: Int get() = if (pageSize == 0) 0 else ceil(items.size.toDouble() / pageSize).toInt()

    override suspend fun setPage(page: Int) = withContext(YAPGL.dispatcher) {
        val clamped = page.coerceIn(0, (totalPages - 1).coerceAtLeast(0))
        this@AutoPaginatedMenu.page = clamped

        for (slot in contentSlots) _slots.remove(slot)

        val start = clamped * pageSize
        val pageItems = items.drop(start).take(pageSize)
        pageItems.forEachIndexed { index, item ->
            val btn = renderItem(item)
            btn.slots = setOf(contentSlots[index])
            _slots[contentSlots[index]] = btn
        }

        onPageChange(clamped)
        if (viewers.isNotEmpty()) refresh()
    }
}
