package chatoverhaul

import chatoverhaul.minimessage.ChatFormatter
import chatoverhaul.mth.MathMessage
import chatoverhaul.replycommand.ReplyCommand
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class ChatOverHaul : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic

        val replyCommand = getCommand("reply")
        val helpCommand = getCommand("messagehelp")

        server.pluginManager.registerEvents(ChatFormatter(), this)
        server.pluginManager.registerEvents(MathMessage(), this)
        if (replyCommand != null) {
            val command = ReplyCommand(replyCommand)
            replyCommand.setExecutor(command)
            replyCommand.tabCompleter = command
            server.pluginManager.registerEvents(command, this)
        }
        if (helpCommand != null) {
            val command = HelpCommand()
            helpCommand.setExecutor(command)
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll()
    }
}