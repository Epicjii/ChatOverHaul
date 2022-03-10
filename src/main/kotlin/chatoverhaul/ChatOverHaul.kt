package chatoverhaul

import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class ChatOverHaul : JavaPlugin() {


    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(ChatChanger(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll()
    }
}