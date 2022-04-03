package chatoverhaul.minimessage

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatFormatter : Listener {
    private val messageManipulator = MiniMessage.miniMessage()

    @EventHandler(priority = EventPriority.HIGH)
    fun reformatChatMessage(event: AsyncChatEvent) {

        val listOfChildMessage = event.message().children()
        val message: String = chatRebuilder(listOfChildMessage) + (event.message() as TextComponent).content()

        val newMessage = messageManipulator.deserialize(message)

        event.message(newMessage)
    }

    private fun chatRebuilder(componentList: List<Component>): String {
        var message = ""
        if (componentList.isNotEmpty()) {
            for (component: Component in componentList) {
                message += chatRebuilder(component.children())
                message += (component as TextComponent).content()
            }
            return message
        }
        return message
    }
}