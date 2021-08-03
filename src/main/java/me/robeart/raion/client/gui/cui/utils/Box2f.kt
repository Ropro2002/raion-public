package me.robeart.raion.client.gui.cui.utils

import com.google.gson.JsonObject
import me.robeart.raion.client.util.Serializable
import net.minecraft.util.math.Vec2f
import kotlin.math.abs

/**
 * @author cookiedragon234 16/Jun/2020
 */
data class Box2f(
	var posX: Float = 0f,
	var posY: Float = 0f,
	var sizeX: Float = 0f,
	var sizeY: Float = 0f
): Serializable {
	val topLeft: Vec2f
		get() = Vec2f(posX, posY)
	val bottomRight: Vec2f
		get() = Vec2f(posX + sizeX, posY + sizeY)
	
	var bottomX: Float
		get() = posX + sizeX
		set(value) {
			sizeX = value - posX
		}
	var bottomY: Float
		get() = posY + sizeY
		set(value) {
			sizeY = value - posY
		}
	
	fun contains(pos: Vec2f): Boolean {
		return pos.x >= posX && pos.y >= posY && pos.x <= bottomX && pos.y <= bottomY
	}
	
	fun getCorners(): Array<Vec2f> {
		return arrayOf(Vec2f(posX, posY), Vec2f(bottomX, posY), Vec2f(bottomX, bottomY), Vec2f(posX, bottomY))
	}
	
	/**
	 * Returns true if any of the corners on another element intersect with the corners on this element
	 */
	fun doCornersIntersect(other: Box2f, allowance: Float = 2f): Boolean = getCornerIntersects(other, allowance) != null
	fun doCornersIntersect(corners1: Array<Vec2f>, corners2: Array<Vec2f>, allowance: Float = 4f): Boolean = getCornerIntersects(corners1, corners2, allowance) != null
	
	/**
	 * Returns the first corner of the other element that intersects with one of the corners on this element
	 */
	fun getCornerIntersects(other: Box2f, allowance: Float = 2f): Vec2f? = getCornerIntersects(this.getCorners(), other.getCorners(), allowance)
	fun getCornerIntersects(corners1: Array<Vec2f>, corners2: Array<Vec2f>, allowance: Float = 4f): Vec2f? {
		return corners2.firstOrNull { c2 ->
			corners1.any { c1 ->
				abs(c1.x - c2.x) <= allowance && abs(c1.y - c2.y) <= allowance
			}
		}
	}
	
	override fun read(jsonObject: JsonObject) {
		this.posX = jsonObject.get("x").asFloat
		this.posY = jsonObject.get("y").asFloat
		this.sizeX = jsonObject.get("width").asFloat
		this.sizeY = jsonObject.get("height").asFloat
	}
	
	override fun write(jsonObject: JsonObject) {
		jsonObject.addProperty("x", this.posX)
		jsonObject.addProperty("y", this.posY)
		jsonObject.addProperty("width", this.sizeX)
		jsonObject.addProperty("height", this.sizeY)
	}
}
