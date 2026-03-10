package dev.echonine.yapgl.test

import dev.echonine.yapgl.YAPGL
import org.bukkit.plugin.java.JavaPlugin

class TestPlugin : JavaPlugin()  {
    override fun onEnable() {
        YAPGL.initialize(this)

        this.server.commandMap.register("testyapgl", TestGUICommand())
    }
}