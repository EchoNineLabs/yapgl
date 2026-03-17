package dev.echonine.yapgl

import com.github.retrooper.packetevents.PacketEvents
import dev.echonine.yapgl.listeners.PacketListener
import kotlinx.coroutines.*
import java.util.concurrent.Executors

object YAPGL {
    internal var dispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    internal var scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val listener = PacketListener()

    fun initialize() {
        PacketEvents.getAPI().eventManager.registerListener(listener)
    }

    fun shutdown() {
        PacketEvents.getAPI().eventManager.unregisterListener(listener)
        scope.cancel()
    }

    const val CONTAINER_ID = 1318
}