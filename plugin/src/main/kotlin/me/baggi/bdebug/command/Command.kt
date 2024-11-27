package me.baggi.bdebug.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import me.baggi.bdebug.App
import org.bukkit.command.CommandSender

@CommandAlias("bdebug")
class Command(val app: App) : BaseCommand() {
    @Subcommand("list")
    fun list(commandSender: CommandSender) {
        app.allPages().forEach { (id, lines) ->
            commandSender.sendMessage("[BDebug] Page $id with ${lines.size} lines")
        }
    }

    @Subcommand("remove")
    fun remove(commandSender: CommandSender, id: String) {
        app.removePage(id)
        commandSender.sendMessage("[BDebug] Removed $id")
    }
}