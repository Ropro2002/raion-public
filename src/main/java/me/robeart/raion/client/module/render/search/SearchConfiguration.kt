package me.robeart.raion.client.module.render.search

import java.io.File
import java.io.PrintWriter
import java.util.Scanner

/**
 * @author cookiedragon234 19/Jun/2020
 */
object SearchConfiguration {
	
	val saveFile = File("raion/search.txt").also {
		if (!it.exists()) {
			it.createNewFile()
		}
	}
	fun load() {
		try {
			Scanner(saveFile.inputStream()).use {
				while (it.hasNextLine()) {
					SearchBlocksManager.addHighlight(
						it.nextLine(),
						true
					)
				}
			}
			if (SearchBlocksManager.toHighlight.isNotEmpty()) {
				return
			}
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		SearchBlocksManager.addHighlight("chest", true)
		SearchBlocksManager.addHighlight("ender_chest", true)
		SearchBlocksManager.addHighlight("shulker", true)
		save()
	}
	fun save() {
		try {
			PrintWriter(saveFile.outputStream()).use {
				for (block in SearchBlocksManager.toHighlight) {
					it.println(block.registryName?.path ?: block.localizedName)
				}
			}
		} catch (t: Throwable) {
			t.printStackTrace()
		}
	}
}
