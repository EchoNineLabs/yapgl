package dev.echonine.yapgl.components

import dev.echonine.yapgl.YAPGL
import dev.echonine.yapgl.events.ComponentClickEvent
import dev.echonine.yapgl.menu.Menu
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class AnimatedButton(
    override var id: String = "",
    override var item: ItemStack = ItemStack(Material.AIR),
    override var slots: Set<Int> = emptySet(),
    override var priority: Int = 0,
    var frames: List<ItemStack> = emptyList(),
    var intervalTicks: Long = 20,
    var onClick: suspend (ComponentClickEvent) -> Unit = {}
) : MenuComponent() {

    var slot: Int
        get() = slots.firstOrNull() ?: -1
        set(value) { slots = setOf(value) }

    private var job: Job? = null
    private var currentFrame = 0

    internal fun start(menu: Menu) {
        if (frames.size <= 1) return
        if (job?.isActive == true) return
        item = frames[0]
        job = YAPGL.scope.launch {
            delay(intervalTicks * 50)
            while (isActive) {
                if (menu.viewers.isEmpty()) break
                currentFrame = (currentFrame + 1) % frames.size
                item = frames[currentFrame]
                menu.refresh()
                delay(intervalTicks * 50)
            }
        }
    }

    internal fun stop() {
        job?.cancel()
        job = null
    }

    override suspend fun onComponentClick(event: ComponentClickEvent) {
        onClick(event)
    }
}
