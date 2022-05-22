package chatoverhaul

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class HelpCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender, command: Command, label: String, args: Array<String>
    ): Boolean {
        val messageFormat = Component.text("MessageFormat, ").color(TextColor.color(0, 239, 255))
            .clickEvent(ClickEvent.openUrl("https://docs.adventure.kyori.net/minimessage/format.html#standard-tags"))
            .hoverEvent(
                HoverEvent.showText(
                    Component.text(
                        """
                   Format:
                   <effect>Affected Text</effect> Unaffected Text
                   Effects: 
                   Color(grey or #8834FF)
                   Decoration(underline or bold)
                   Click(uses Click events)
                   Hover
                   Click the message to see more!
            """.trimIndent()
                    )
                )
            )
        val mathMessage = Component.text("MathMessage, ").color(TextColor.color(0, 239, 255)).hoverEvent(
            HoverEvent.showText(
                Component.text(
                    """
                Format: $(expr)
                Acceptable Operations: +, -, /, *, ^, % 
                neg(expr) - Makes it negative
                  """.trimIndent()
                )
            )
        )
        val replyMessage = Component.text("ReplyMessage").color(TextColor.color(0, 239, 255))
            .hoverEvent(HoverEvent.showText(Component.text("/r to reply to whisper")))

        sender.sendMessage(
            Component.join(
                JoinConfiguration.noSeparators(),
                Component.text("Hover Over For Help: "),
                messageFormat,
                mathMessage,
                replyMessage
            )
        )
        return true
    }
}