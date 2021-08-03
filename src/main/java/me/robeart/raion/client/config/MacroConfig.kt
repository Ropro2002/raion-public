package me.robeart.raion.client.config

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.robeart.raion.client.macro.Macro
import me.robeart.raion.client.managers.MacroManager
import me.robeart.raion.client.util.Key
import me.robeart.raion.client.util.Saveable
import java.io.File

/**
 * @author cookiedragon234 02/Jun/2020
 */
object MacroConfig: Saveable {
	override val configFile: File = File("raion/macros.json")
	override val name: String
		get() = "Macros Config"
	
	override fun write(jsonObject: JsonObject) {
		val macrosObj = JsonArray()
		for ((key, macro) in MacroManager.macros) {
			val macroJson = JsonObject()
			macroJson.addProperty("Key", key.toString())
			macroJson.addProperty("Command", macro.commands)
			macrosObj.add(macroJson)
		}
		jsonObject.add("Macros", macrosObj)
	}
	
	override fun read(jsonObject: JsonObject) {
		val macrosObj = jsonObject.get("Macros")
		macrosObj as JsonArray
		for (entry in macrosObj) {
			entry as JsonObject
			val keyName = entry.get("Key").asString
			val command = entry.get("Command").asString
			val key = Key.fromName(keyName)
			val macro = Macro(key, command)
			MacroManager.macros[key] = macro
		}
	}
}
