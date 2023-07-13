package chatoverhaul

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage

object ChatUtilities {
    private val messageManipulator = MiniMessage.miniMessage()
    fun chatOrganizer(component: Component): List<Component> {
        val listOfComponents = mutableListOf<Component>()

        if (component.children().isEmpty()) {
            return listOf(component)
        }

        for (childComponent: Component in component.children()) {
            listOfComponents.addAll(chatOrganizer(childComponent))
        }
        return listOfComponents
    }

    fun Component.getContent(): String {
        return (this as TextComponent).content()
    }

    fun Component.format(): Component {
        val message = this
        val reorganizedMessages = chatOrganizer(message)

        val newMessage = reorganizedMessages.map {
            if (it.hasStyling()) {
                it
            } else {
                messageManipulator.deserialize(it.getContent())
            }
        }
        return Component.join(JoinConfiguration.noSeparators(), newMessage)
    }
}