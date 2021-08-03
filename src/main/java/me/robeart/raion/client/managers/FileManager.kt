package me.robeart.raion.client.managers

import net.minecraft.client.Minecraft
import java.io.File

/**
 * @author Robeart
 */
class FileManager {

    val mc = Minecraft.getMinecraft()

    val dir = File(mc.gameDir, "raion")

    init {
        setupFolder()
    }

    fun setupFolder() {
        if (!dir.exists()) dir.mkdir()

    }
}
