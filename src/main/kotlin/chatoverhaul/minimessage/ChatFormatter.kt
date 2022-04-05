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

        val eventMessage = event.message()
        val reorganizedMessages = chatOrganizer(eventMessage)

        val newMessage = reorganizedMessages.map {
            if(it.hasStyling()) {
                it
            }  else {
                messageManipulator.deserialize((it as TextComponent).content())
            }
        }
        event.message(Component.join(JoinConfiguration.noSeparators(), newMessage))
    }

    private fun chatOrganizer(component: Component): List<Component> {
        val listOfComponents = mutableListOf<Component>()

        if (component.children().isEmpty()) {
            return listOf(component)
        }

        for (childComponent: Component in component.children()) {
            listOfComponents.addAll(chatOrganizer(childComponent))
        }
        return listOfComponents
    }
}