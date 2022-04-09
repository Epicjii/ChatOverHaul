package chatoverhaul.replycommand

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.permissions.Permissible
import org.bukkit.permissions.PermissionDefault


class ReplyCommand(private val replyCommand: PluginCommand) : CommandExecutor, TabCompleter, Listener {

    private val replierToTargetMap = mutableMapOf<CommandSender, CommandSender>()

    private val whisperCommands = setOf("w", "msg", "tell")

    private val aliases = replyCommand.aliases

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val message = args.joinToString(" ")
        val replyTarget = replierToTargetMap[sender]

        if (replyTarget == null) {
            sender.sendMessage(
                Component.text("Nobody has messaged you yet.")
            )
            return true
        } else {
            sender.sendMessage(
                Component.text("You reply: $message")
                    .decoration(TextDecoration.ITALIC, true)
                    .color(TextColor.fromHexString("#AAAAAA"))
            )
            replyTarget.sendMessage(
                Component.text(sender.name + " replies: " + message)
                    .decoration(TextDecoration.ITALIC, true)
                    .color(TextColor.fromHexString("#AAAAAA"))
            )
            replierToTargetMap[replyTarget] = sender
        }
        return true
    }

    @EventHandler
    fun messageTracker(event: PlayerCommandPreprocessEvent) {
        if (event.isCancelled) {
            return
        }
        val sender = event.player
        val splitCommand = event.message.split(" ")
        var command = splitCommand[0]
        if (command[0] == '/') {
            command = command.substring(1)
        }
        if (commandIsNotValid(sender, command, splitCommand)) {
            return
        }
        for (recipient in whisperCommandRecipients(sender, command, splitCommand)) {
            replierToTargetMap[recipient] = event.player
        }
    }

    private fun commandIsNotValid(sender: Permissible, command: String, splitCommand: List<String>): Boolean {
        if (commandLengthCorrect(command, splitCommand)) {
            if (aliases.contains(command)) {
                val permission = replyCommand.permission
                return permission != null && !sender.hasPermission(permission)
            }
            if (whisperCommands.contains(command)) {
                return senderNotPermittedToMessageRecipient(sender, splitCommand[1])
            }
        }
        return true
    }

    private fun commandLengthCorrect(command: String, splitCommand: List<String>): Boolean {
        return if (aliases.contains(command)) {
            splitCommand.size >= 2
        } else splitCommand.size >= 3
    }

    private fun senderNotPermittedToMessageRecipient(sender: Permissible, recipient: String): Boolean {
        return recipient[0] == '@' && !senderPermittedToUseSelector(sender)
    }

    private fun senderPermittedToUseSelector(sender: Permissible): Boolean {
        return sender.hasPermission(PermissionDefault.OP.toString())
    }

    private fun whisperCommandRecipients(
        sender: CommandSender,
        command: String,
        splitCommand: List<String>?
    ): List<CommandSender> {
        if (aliases.contains(command)) {
           val target = replierToTargetMap[sender]
            return if (target == null) emptyList()
            else listOf(target)
        }
        val recipient = splitCommand!![1]
        return Bukkit.getServer().selectEntities(sender, recipient)
    }

    @EventHandler
    fun messageTracker(event: ServerCommandEvent) {
        if (event.isCancelled) {
            return
        }
        val sender = event.sender
        val splitCommand = event.command.split(" ")
        val command = splitCommand[0]
        if (commandIsNotValid(sender, command, splitCommand)) {
            return
        }
        for (recipient in whisperCommandRecipients(sender, command, splitCommand)) {
            replierToTargetMap[recipient] = sender
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return emptyList()
    }
}