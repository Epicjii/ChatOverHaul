package chatoverhaul.minimessage

import chatoverhaul.ChatUtilities
import chatoverhaul.ChatUtilities.getContent
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.BroadcastMessageEvent

class ChatFormatter : Listener {
    private val messageManipulator = MiniMessage.miniMessage()

    @EventHandler
    fun reformatChatMessage(event: AsyncChatEvent) {

        val eventMessage = event.message()
        val reorganizedMessages = ChatUtilities.chatOrganizer(eventMessage)

        val newMessage = reorganizedMessages.map {
            if (it.hasStyling()) {
                it
            } else {
                messageManipulator.deserialize(it.getContent())
            }
        }
        event.message(Component.join(JoinConfiguration.noSeparators(), newMessage))
    }

    @EventHandler
    fun reformatChatMessage(event: BroadcastMessageEvent) {

        val eventMessage = event.message()
        val reorganizedMessages = ChatUtilities.chatOrganizer(eventMessage)

        val newMessage = reorganizedMessages.map {
            if (it.hasStyling()) {
                it
            } else {
                messageManipulator.deserialize(it.getContent())
            }
        }
        event.message(Component.join(JoinConfiguration.noSeparators(), newMessage))
    }
}