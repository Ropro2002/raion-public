package me.robeart.raion.client.command

import me.robeart.raion.client.util.ChatUtils
import net.minecraft.nbt.NBTTagCompound
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.File

/**
 * @author cookiedragon234 15/May/2020
 */
object NbtDumpCommand: Command("nbtdump", "nbtdump", "nbtdump") {
	val out = File("raion/nbtdump.nbt")
	
	val write by lazy {
		NBTTagCompound::class.java.getDeclaredMethod("write", DataOutput::class.java).also {
			it.isAccessible = true
		}
	}
	
	override fun call(args: Array<out String>?) {
		val item = if (mc.player.heldItemMainhand.isEmpty == false) {
			mc.player.heldItemMainhand
		} else if (mc.player.heldItemOffhand.isEmpty == false) {
			mc.player.heldItemOffhand
		} else {
			ChatUtils.error("Please hold an item")
			return
		}
		
		val nbt = NBTTagCompound()
		item.writeToNBT(nbt)
		DataOutputStream(out.outputStream()).use {
			write(nbt, it)
		}
	}
}
