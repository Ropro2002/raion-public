package me.robeart.raion.client.command

import me.robeart.raion.client.managers.Waypoint2
import me.robeart.raion.client.managers.Waypoint3
import me.robeart.raion.client.managers.WaypointManager
import me.robeart.raion.client.util.ChatUtils

/**
 * @author cookiedragon234 24/May/2020
 */
object WaypointCommand: Command("Waypoint", "Add and remove waypoints", ".waypoint [add/remove/here] name x (?y) z") {
	override fun call(args: Array<out String>) {
		if (args.isNotEmpty()) {
			if (args[0] == "add" || args[0] == "remove") {
				val waypoint = when (args.size) {
					2    -> {
						if (args[0] == "remove") {
							if (WaypointManager.waypoints.removeFirst { it.name.equals(args[1], true) }) {
								ChatUtils.message("Removed ${args[1]}")
							} else {
								ChatUtils.error("Couldn't find ${args[1]}")
							}
							return
						}
						ChatUtils.error(this.usage)
						return
					}
					4    -> Waypoint2(args[1], args[2].toInt(), args[3].toInt())
					5    -> Waypoint3(args[1], args[2].toInt(), args[3].toInt(), args[4].toInt())
					else -> {
						ChatUtils.error(this.usage)
						return
					}
				}
				
				if (args[0] == "add") {
					WaypointManager.waypoints.add(waypoint)
					ChatUtils.message("Added $waypoint")
					return
				}
			} else if (args[0] == "here") {
				val e = mc.renderViewEntity ?: mc.player ?: error("No Selectable Entity")
				val name = if (args.size >= 2) args[1] else "Unnamed"
				val waypoint = Waypoint3(name, e.posX.toInt(), e.posY.toInt(), e.posZ.toInt())
				WaypointManager.waypoints.add(waypoint)
			} else if (args[0] == "clear") {
				WaypointManager.waypoints.clear()
			} else if (args[1] == "list") {
				ChatUtils.message(buildString {
					append(WaypointManager.waypoints.size)
					append(" waypoints:\n")
					append(WaypointManager.waypoints.joinToString("\n"))
				})
			}
		}
		ChatUtils.error(this.usage)
	}
	
	fun <T> MutableCollection<T>.removeFirst(filter: (T) -> Boolean): Boolean {
		val each = this.iterator()
		while (each.hasNext()) {
			if (filter(each.next())) {
				each.remove()
				return true
			}
		}
		return false
	}
}
