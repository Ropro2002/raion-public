package me.robeart.raion.client.command

import me.robeart.raion.client.module.render.search.SearchBlocksManager
import me.robeart.raion.client.module.render.search.SearchModule
import me.robeart.raion.client.util.ChatUtils

/**
 * @author cookiedragon234 17/May/2020
 */
object SearchCommand: Command("Search", "Add items to search list", "search [add/remove/list]") {
	override fun call(args: Array<out String>?) {
		if (args?.isNotEmpty() != true) {
			ChatUtils.error(super.getUsage())
			return
		}
		
		when (args[0]) {
			"add"    -> {
				if (args.size < 2) {
					ChatUtils.error(".search add shulker")
				}
				if (SearchBlocksManager.addHighlight(args[1], true)) {
					ChatUtils.message("Added ${args[1]}")
				} else {
					ChatUtils.error("Couldnt find ${args[1]}")
				}
			}
			"remove" -> {
				if (args.size < 2) {
					ChatUtils.error(".search remove shulker")
				}
				if (SearchBlocksManager.addHighlight(args[1], false)) {
					ChatUtils.message("Removed ${args[1]}")
				} else {
					ChatUtils.error("Couldnt find ${args[1]}")
				}
			}
			"list"   -> {
				val highlighted = SearchBlocksManager.getToHighlighted()
				ChatUtils.message("Highlighting ${highlighted.size} blocks:\n${highlighted.joinToString("\n")}")
			}
			"clear"  -> {
				ChatUtils.message("Cleared ${SearchBlocksManager.clearHighlighted()} blocks")
			}
			else     -> ChatUtils.error(super.getUsage())
		}
	}
}
