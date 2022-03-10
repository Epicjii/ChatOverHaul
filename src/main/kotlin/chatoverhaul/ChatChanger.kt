package chatoverhaul

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatChanger : Listener {
    private val messageManipulator = MiniMessage.miniMessage()

    @EventHandler
    fun chatChanger(event: AsyncChatEvent) {

        val message = event.message() as TextComponent
        val newMessage = messageManipulator.deserialize(message.content())
        event.message(newMessage)
    }
}