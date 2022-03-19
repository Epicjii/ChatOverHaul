package chatoverhaul

import chatoverhaul.minimessage.ChatFormatter
import chatoverhaul.replycommand.ReplyCommand
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class ChatOverHaul : JavaPlugin() {

    var command : ReplyCommand? = null
    override fun onEnable() {
        // Plugin startup logic

        val replyCommand = getCommand("reply")

        server.pluginManager.registerEvents(ChatFormatter(), this)
        if (replyCommand != null) {
            val command = ReplyCommand(replyCommand)
            replyCommand.setExecutor(command)
            replyCommand.tabCompleter = command
            server.pluginManager.registerEvents(command, this)
        }

    }

    override fun onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll()
    }
}