package me.robeart.raion.client.managers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.robeart.raion.client.util.Saveable
import java.io.File

/**
 * @author cookiedragon234 24/May/2020
 */
object WaypointManager: Saveable {
	var waypoints: MutableList<Waypoint> = ArrayList()
	
	override val configFile: File = File("raion/waypoints.json")
	
	override fun write(jsonObject: JsonObject) {
		val jsonWaypoints = jsonObject.get("waypoints").asJsonArray
		
		waypoints = ArrayList(jsonWaypoints.size())
		
		jsonWaypoints.forEach {
			val obj = it.asJsonObject
			val name = obj.get("name").asString
			val x = obj.get("x").asInt
			val y = obj.get("y")?.asInt
			val z = obj.get("z").asInt
			
			if (y != null) {
				waypoints.add(Waypoint3(name, x, y, z))
			} else {
				waypoints.add(Waypoint2(name, x, z))
			}
		}
	}
	
	override fun read(jsonObject: JsonObject) {
		val jsonArr = JsonArray()
		
		waypoints.forEach {
			val obj = JsonObject()
			jsonArr.add(obj)
			if (it is Waypoint2) {
				obj.addProperty("name", it.name)
				obj.addProperty("x", it.x)
				obj.addProperty("z", it.z)
			} else {
				it as Waypoint3
				obj.addProperty("name", it.name)
				obj.addProperty("x", it.x)
				obj.addProperty("y", it.y)
				obj.addProperty("z", it.z)
			}
		}
		
		jsonObject.add("waypoints", jsonArr)
	}
}

interface Waypoint {
	val name: String
}

data class Waypoint3(
	override val name: String,
	val x: Int,
	val y: Int,
	val z: Int
): Waypoint {
	override fun toString(): String {
		return buildString {
			append(name)
			append('(')
			append(x)
			append(',')
			append(y)
			append(',')
			append(z)
			append(')')
		}
	}
}

data class Waypoint2(
	override val name: String,
	val x: Int,
	val z: Int
): Waypoint {
	override fun toString(): String {
		return buildString {
			append(name)
			append('(')
			append(x)
			append(',')
			append(z)
			append(')')
		}
	}
}
