package chatoverhaul.replycommand

import com.google.common.collect.Lists
import com.google.common.collect.Sets
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
import org.jetbrains.annotations.NotNull
import java.util.*


class ReplyCommand(replyCommand: PluginCommand?) : CommandExecutor, TabCompleter, Listener {

    private val replierToTargetMap = HashMap<CommandSender?, CommandSender>()

    private val whisperCommands = Collections.unmodifiableSet(
        Sets.newHashSet("w", "msg", "tell")
    )
    private var aliases: Set<String>? = null
    private var replyCommand: PluginCommand? = null

    init {
        this.replyCommand = replyCommand
        if (replyCommand != null) {
            aliases = Sets.newHashSet(replyCommand.aliases)
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
//        val message = java.lang.String.join(" ", *args)
        val message = args.joinToString { " " }
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
    fun messageTracker(@NotNull event: PlayerCommandPreprocessEvent) {
        if (event.isCancelled) {
            return
        }
        val sender = event.player
        val splitCommand = event.message.split(" ").toTypedArray()
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

    private fun commandIsNotValid(sender: Permissible, command: String, @NotNull splitCommand: Array<String>): Boolean {
        if (commandLengthCorrect(command, splitCommand)) {
            if (aliases!!.contains(command)) {
                val permission = replyCommand!!.permission
                return permission != null && !sender.hasPermission(permission)
            }
            if (whisperCommands.contains(command)) {
                return senderNotPermittedToMessageRecipient(sender, splitCommand[1])
            }
        }
        return true
    }

    private fun commandLengthCorrect(command: String, splitCommand: Array<String>): Boolean {
        return if (aliases!!.contains(command)) {
            splitCommand.size >= 2
        } else splitCommand.size >= 3
    }

    private fun senderNotPermittedToMessageRecipient(sender: Permissible, @NotNull recipient: String): Boolean {
        return recipient[0] == '@' && !senderPermittedToUseSelector(sender)
    }

    private fun senderPermittedToUseSelector(@NotNull sender: Permissible): Boolean {
        return sender.hasPermission(PermissionDefault.OP.toString())
    }

    @NotNull
    private fun whisperCommandRecipients(
        sender: CommandSender,
        command: String,
        splitCommand: Array<String>?
    ): List<CommandSender?> {
        if (aliases!!.contains(command)) {
            return if (replierToTargetMap.containsKey(sender)) {
                Lists.newArrayList(replierToTargetMap[sender])
            } else emptyList<CommandSender>()
        }
        val recipient = splitCommand!![1]
        return Bukkit.getServer().selectEntities(sender, recipient)
    }

    @EventHandler
    fun messageTracker(@NotNull event: ServerCommandEvent) {
        if (event.isCancelled) {
            return
        }
        val sender = event.sender
        val splitCommand = event.command.split(" ").toTypedArray()
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
    ): MutableList<String>? {
        return Collections.emptyList()
    }
}