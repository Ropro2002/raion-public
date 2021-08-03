package me.robeart.raion.client.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File


/**
 * @author cookiedragon234 02/May/2020
 */
interface Saveable: Configurable, Serializable {
	val configFile: File
	val name: String
		get() = this::class.java.name
	
	override fun load() {
		if (!configFile.exists()) {
			configFile.createNewFile()
			return
		}
		try {
			configFile.reader().use {
				val obj = JsonParser().parse(it)?.asJsonObject ?: error("Corrupt configuration file for $name")
				read(obj)
				println("Restored $name")
			}
		} catch (t: Throwable) {
			throw IllegalStateException("Error while loading configuration for $name", t)
		}
	}
	
	override fun save() {
		try {
			configFile.writer().use {
				val obj = JsonObject()
				write(obj)
				val gson = GsonBuilder()
					.setPrettyPrinting()
					.create()
				gson.toJson(obj, it)
				println("Saved $name")
			}
		} catch (t: Throwable) {
			throw IllegalStateException("Error while reading configuration for $name", t)
		}
	}
}

interface Serializable {
	fun write(jsonObject: JsonObject)
	fun read(jsonObject: JsonObject)
}

interface Configurable {
	fun load()
	fun save()
}
