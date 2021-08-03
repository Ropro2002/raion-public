package me.robeart.raion.client.gui.cui.anchor

import com.google.gson.JsonObject
import me.robeart.raion.client.gui.cui.RaionCui
import me.robeart.raion.client.gui.cui.element.CuiElement
import java.util.UUID
import kotlin.math.abs

/**
 * @author cookiedragon234 17/Jun/2020
 */
abstract class AnchorPoint(val direction: AnchorPointDirection) {
	/**
	 * Snaps the given element to this anchor point
	 */
	abstract fun snap(element: CuiElement)
	
	companion object {
		fun processAnchorPoints(element: CuiElement) {
			val pos = element.position
			
			val thisCorners = element.position.getCorners()
			
			for (other in RaionCui.elements) {
				if (other == element) continue
				
				val otherCorners = other.position.getCorners()
				val intersect = other.position.getCornerIntersects(thisCorners, otherCorners, 4f)
				if (intersect != null) {
					element.anchor = ElementAnchorPoint(other, AnchorPointDirection.values()[otherCorners.indexOf(intersect)])
					return
				}
			}
		}
		fun writeAnchorPoint(anchorPoint: AnchorPoint, jsonObject: JsonObject) {
			when (anchorPoint) {
				is ElementAnchorPoint -> {
					jsonObject.addProperty("type", "element")
					jsonObject.addProperty("element", anchorPoint.uuid.toString())
					jsonObject.addProperty("direction", anchorPoint.direction.name)
				}
				else -> error("Unrecognised anchor point type ${anchorPoint::class.java}")
			}
		}
		fun readAnchorPoint(jsonObject: JsonObject): AnchorPoint {
			return when (val type = jsonObject.get("type").asString) {
				"element" -> {
					val uuid = UUID.fromString(jsonObject.get("element").asString)
					val element = RaionCui.elements.firstOrNull { it.uuid == uuid }
					val direction = AnchorPointDirection.valueOf(jsonObject.get("direction").asString)
					ElementAnchorPoint(element, direction, uuid)
				}
				else -> error("Unrecognised anchor point type ${type::class.java}")
			}
		}
	}
}
