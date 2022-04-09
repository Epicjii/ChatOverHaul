package chatoverhaul

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

object ChatUtilities {
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
}