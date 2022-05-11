package chatoverhaul.mth

import chatoverhaul.ChatUtilities
import chatoverhaul.ChatUtilities.getContent
import com.notkamui.keval.KevalInvalidExpressionException
import com.notkamui.keval.KevalInvalidSymbolException
import com.notkamui.keval.KevalZeroDivisionException
import com.notkamui.keval.keval
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
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
                    calculator(matchResult)
                }
                )
            }
        }
        event.message(Component.join(JoinConfiguration.noSeparators(), messageString))
    }

    private fun calculator(match: MatchResult): String {
        val result: Double
        try {
            result = match.groups[1]!!.value.keval()
        } catch (err: KevalZeroDivisionException) {
            return "Error: Cannot Divide by Zero"
        } catch (err: KevalInvalidSymbolException) {
            return "Error: Invalid Symbol in Expression"
        } catch (err: KevalInvalidExpressionException) {
            return "Error: Expression is invalid"
        }
        if (result - result.roundToInt() != 0.0) {
            return result.toString()
        }
        return result.roundToInt().toString()
    }
}