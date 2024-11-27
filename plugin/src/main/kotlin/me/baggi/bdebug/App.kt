package me.baggi.bdebug

import co.aikar.commands.PaperCommandManager
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.nio.charset.StandardCharsets

class App : JavaPlugin(), BDebugApi {
    private val gson: Gson = Gson()
    private val pages: MutableMap<String, List<String>> = mutableMapOf()

    override fun onEnable() {
        server.messenger.registerOutgoingPluginChannel(this, "debugmod:info")

        PaperCommandManager(this).apply {
            registerCommand(Command(this@App))
        }

        server.scheduler.runTaskTimerAsynchronously(this, Runnable {
            server.onlinePlayers.filter { it.isOp }.forEach {
                sendDebugData(it, pages.values)
            }
        }, 0, 1)

        server.scheduler.runTaskTimerAsynchronously(this, Runnable {
            addOrChangePage("default", listOf(
                "TPS: ${server.tps[0]}",
                "Server time: ${System.currentTimeMillis()}"
            ))
        }, 0, 1)
    }

    fun allPages(): Map<String, List<String>> = pages.toMap()

    private fun sendDebugData(player: Player, data: Collection<List<String>>) {
        val jsonData = gson.toJson(data)

        val out = ByteStreams.newDataOutput()
        val jsonBytes = jsonData.toByteArray(StandardCharsets.UTF_8)

        out.writeInt(jsonBytes.size)
        out.write(jsonBytes)

        player.sendPluginMessage(this, "debugmod:info", out.toByteArray())
    }

    override fun addOrChangePage(id: String, lines: List<String>) {
        pages[id] = lines
    }

    override fun removePage(id: String) {
        pages.remove(id)
    }
}