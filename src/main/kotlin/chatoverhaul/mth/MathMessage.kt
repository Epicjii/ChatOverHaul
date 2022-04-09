package chatoverhaul.mth

import chatoverhaul.ChatUtilities
import chatoverhaul.ChatUtilities.getContent
import com.notkamui.keval.keval
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import kotlin.math.roundToInt

class MathMessage : Listener {

    private val parser = Regex("\\$\\((.*)\\)")

    @EventHandler(priority = EventPriority.LOW)
    fun getMessage(event: AsyncChatEvent) {
        val message = ChatUtilities.chatOrganizer(event.message())

        val messageString = message.map {
            if (it.hasStyling()) {
                it
            } else {
                Component.text(parser.replace(it.getContent()) { matchResult ->
                    calculator(matchResult) }
                )
            }
        }
        event.message(Component.join(JoinConfiguration.noSeparators(), messageString))
    }

    private fun calculator(match: MatchResult): String {
        val result = match.groups[1]!!.value.keval()
        if (result - result.roundToInt() != 0.0) {
            return result.toString()
        }
        return result.roundToInt().toString()
    }
}