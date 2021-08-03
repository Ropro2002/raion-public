package me.robeart.raion.client.command

import me.robeart.raion.client.macro.Macro
import me.robeart.raion.client.managers.MacroManager
import me.robeart.raion.client.util.ChatUtils
import me.robeart.raion.client.util.Key

object MacroCommand: Command(
	"Macro",
	arrayOf("Mac", "m", "macros"),
	"Allows the addition or removal of macros",
	"macro add/remove/set/list <key> <commands> (separate lines with \";\")"
) {
	override fun call(args: Array<String>) {
		if (args.size == 1) {
			val control = args[0]
			
			if (control.equals("list", true)) {
				val out = buildString {
					append("Listing ${MacroManager.size} macros:\n")
					append(MacroManager.entries.joinToString("\n", transform = { entry ->
						"${entry.key} -> ${entry.value.commands}"
					}))
				}.trim()
				ChatUtils.message(out)
				return
			}
		} else if (args.size >= 2) {
			val control = args[0]
			
			val keyName = args[1]
			val key = try {
				Key.fromName(keyName)
			} catch (t: Throwable) {
				t.printStackTrace()
				ChatUtils.error("Couldn't find key $keyName")
				return
			}
			if (control.equals("add", true)) {
				val runArgs = buildString {
					for (arg in 2 until args.size) {
						append(args[arg] + " ")
					}
				}
				val existing = MacroManager[key]
				if (existing != null) {
					existing.commands += ";$runArgs"
					ChatUtils.message("Appended commands to existing macro $key")
					return
				} else {
					val macro = Macro(key, runArgs)
					MacroManager[key] = macro
					ChatUtils.message("Added macro for key $key")
					return
				}
			} else if (control.equals("set", true)) {
				val runArgs = buildString {
					for (arg in 2 until args.size) {
						append(args[arg] + " ")
					}
				}
				val existing = MacroManager[key]
				if (existing != null) {
					existing.commands = runArgs
					ChatUtils.message("Set commands for existing macro $key")
					return
				} else {
					val macro = Macro(key, runArgs)
					MacroManager[key] = macro
					ChatUtils.message("Added macro for key $key")
					return
				}
			} else if (control.equals("remove", true)) {
				if (MacroManager.remove(key) != null) {
					ChatUtils.message("Removed macro for key $key")
					return
				} else {
					ChatUtils.error("Couldn't find macro for key $key")
					return
				}
			} else if (control.equals("list", true)) {
				ChatUtils.message("Macro for key $key: ${MacroManager[key]?.commands}")
				return
			}
		}
		ChatUtils.error(this.usage)
	}
}
