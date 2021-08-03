package me.robeart.raion.client.command

import me.robeart.raion.client.Raion

/**
 * @author cookiedragon234 20/Apr/2020
 */
object ModuleListCommand: Command("modulelist", "print a module list", "modulelist") {
	override fun call(args: Array<out String>) {
		println(buildString {
			append("-------------Modules:-------------\n")
			for (module in Raion.INSTANCE.moduleManager.moduleList) {
				append(module.name)
				append(": ")
				append(module.description)
				append('\n')
				// TODO: Collate values in a tree structure representing parent child relationships
				// e.g.:
				// Crystal Aura
				//      - Place: Boolean
				//          - Place Delay: Int
				//      - Break: Boolean
				//          -- Break Delay: Int
				for (value in module.values) {
					append("\t- ")
					append(value.name)
					append(": ")
					append(value.value::class.java.simpleName)
					append('\n')
				}
			}
			
			append("\n-------------Commands:-------------\n")
			for (command in Raion.INSTANCE.commandManager.commandList) {
				if (command == this@ModuleListCommand) continue
				
				append(command.name)
				append(": ")
				append(command.description)
				append('\n')
			}
		})
	}
}
