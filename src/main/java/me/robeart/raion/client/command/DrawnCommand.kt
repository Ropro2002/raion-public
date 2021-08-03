package me.robeart.raion.client.command

import me.robeart.raion.client.Raion
import me.robeart.raion.client.util.ChatUtils

/**
 * @author Robeart
 */
object DrawnCommand: Command(
	"Drawn",
	arrayOf("d", "visible"),
	"Disable modules being drawn on the arraylist",
	"drawn (module)"
) {
	override fun call(args: Array<String>) {
		if (args.isEmpty()) {
			ChatUtils.message("Please specify a module")
			return
		} else {
			val m = Raion.INSTANCE.moduleManager.getModule(args[0])
			if (m == null) ChatUtils.message(args[0] + " is not a valid module")
			else {
				m.visible = m.visible.not()
				ChatUtils.message(m.name + " is now " + (if (m.visible) "visible" else "hidden"))
			}
			return
		}
	}
}
