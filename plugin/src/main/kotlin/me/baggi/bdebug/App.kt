package me.baggi.bdebug

import co.aikar.commands.PaperCommandManager
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import me.baggi.bdebug.command.Command
import me.baggi.bdebug.exception.PageDuplicateException
import me.baggi.bdebug.exception.PageNotFoundException
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.nio.charset.StandardCharsets
import java.util.UUID

class App : JavaPlugin(), BDebugApi {
    private val gson: Gson = Gson()
    private val pages: MutableMap<String, PageData> = mutableMapOf()

    override fun onEnable() {
        server.messenger.registerOutgoingPluginChannel(this, "debugmod:info")

        saveDefaultConfig()

        PaperCommandManager(this).apply {
            registerCommand(Command(this@App))
        }

        if (config.getBoolean("add-default-pages")) {
            addPage("default", PageData("default", emptyList()))
            addPage("another-page", PageData("another-page", emptyList()))

            server.scheduler.runTaskTimerAsynchronously(this, Runnable {
                changeLines(
                    "default",
                    "Вау! Это новая страничка??",
                    "",
                    "Серверное время: ${System.currentTimeMillis()}",
                    "TPS: ${server.tps[0]}",
                    "",
                    "52"
                )
                changeLines(
                    "another-page",
                    "А это другая страничка!",
                    "",
                    "Тут буковки с циферками бегают: ${UUID.randomUUID()}"
                )
            }, 0, 1)
        }

        server.scheduler.runTaskTimerAsynchronously(this, Runnable {
            server.onlinePlayers.filter { it.isOp }.forEach {
                sendDebugData(it)
            }
        }, 0, config.getLong("updating-period"))
    }

    fun allPages(): List<PageData> = pages.values.toList()

    private fun sendDebugData(player: Player) {
        val jsonData = gson.toJson(pages.values)

        val out = ByteStreams.newDataOutput()
        val jsonBytes = jsonData.toByteArray(StandardCharsets.UTF_8)

        out.writeInt(jsonBytes.size)
        out.write(jsonBytes)

        player.sendPluginMessage(this, "debugmod:info", out.toByteArray())
    }

    override fun addPage(id: String, pageData: PageData) {
        if (pages.containsKey(id)) throw PageDuplicateException(id)

        pages[id] = pageData
    }

    override fun changeLines(pageId: String, list: List<String>) {
        if (!pages.containsKey(pageId)) throw PageNotFoundException(pageId)

        pages[pageId]!!.lines = list
    }

    override fun changeLines(pageId: String, vararg lines: String) = changeLines(pageId, lines.toList())

    override fun removePage(id: String) {
        pages.remove(id)
    }
}