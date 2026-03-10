package dev.echonine.yapgl.extenstions

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import org.bukkit.event.inventory.ClickType


fun WrapperPlayClientClickWindow.WindowClickType.toBukkitClickType(): ClickType {
    return when (this) {
        WrapperPlayClientClickWindow.WindowClickType.PICKUP -> ClickType.LEFT
        WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE -> ClickType.SHIFT_LEFT
        WrapperPlayClientClickWindow.WindowClickType.CLONE -> ClickType.MIDDLE
        WrapperPlayClientClickWindow.WindowClickType.THROW -> ClickType.DROP
        else -> ClickType.UNKNOWN
    }
}