package chatoverhaul.minimessage

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatFormatter : Listener {
    private val messageManipulator = MiniMessage.miniMessage()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun reformatChatMessage(event: AsyncChatEvent) {

        val eventMessage = mutableListOf(event.message())
        val gatewayMessage = mutableListOf<Component>()
        val miniMessages = mutableListOf<Component>()

        val reorganizedMessages = chatOrganizer(eventMessage)

        for (component: Component in reorganizedMessages) {
            if (component.hasStyling()) {
                gatewayMessage.add(component)
            } else {
                miniMessages.add(component)
            }
        }

        val message: String = miniMessages.joinToString("") {
            (it as TextComponent).content()
        }

        val newMessage = messageManipulator.deserialize(message)

        event.message(Component.join(JoinConfiguration.noSeparators(), gatewayMessage + newMessage))
    }

    private fun chatOrganizer(componentList: List<Component>): List<Component> {
        val listOfComponents = mutableListOf<Component>()

        for (component: Component in componentList) {
            if (component.children().isNotEmpty()) {
                listOfComponents.addAll(component.children())
                listOfComponents.addAll(chatOrganizer(listOfComponents))
            } else {
                listOfComponents.add(component)
            }
        }
        return listOfComponents.distinct()
    }
}