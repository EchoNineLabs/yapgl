package dev.echonine.yapgl

import com.github.retrooper.packetevents.PacketEvents
import dev.echonine.yapgl.listeners.PacketListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.Executors

object YAPGL {
    internal var plugin: JavaPlugin? = null
    internal var dispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    internal var scope = CoroutineScope(dispatcher)

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin

        PacketEvents.getAPI().eventManager.registerListener(PacketListener())
    }

    val CONTAINER_ID = 1318
}