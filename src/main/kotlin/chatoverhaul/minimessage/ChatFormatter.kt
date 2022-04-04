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

        val listOfChildMessages = event.message().children()
        val gatewayMessage = mutableListOf<Component>()
        val miniMessages = mutableListOf<Component>()

        if (listOfChildMessages.isNotEmpty()) {
            for (component: Component in listOfChildMessages) {
                if (component.hasStyling()) {
                    gatewayMessage.add(component)
                } else {
                    miniMessages.add(component)
                }
            }
        } else {
            miniMessages.add(event.message())
        }

        val message: String = miniMessages.joinToString("") {
            (it as TextComponent).content()
        }

        val newMessage = messageManipulator.deserialize(message)

        event.message(Component.join(JoinConfiguration.noSeparators(),gatewayMessage + newMessage))
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