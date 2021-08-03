package me.robeart.raion.client.gui.cui.anchor

import me.robeart.raion.client.gui.cui.element.CuiElement
import net.minecraft.util.math.Vec2f

/**
 * @author cookiedragon234 17/Jun/2020
 */
class CoordinateAnchorPoint(val snapTo: Vec2f, direction: AnchorPointDirection): AnchorPoint(direction) {
	override fun snap(element: CuiElement) {
		when (direction) {
			AnchorPointDirection.TOP_LEFT -> {
				element.position.posX = snapTo.x
				element.position.posY = snapTo.y
			}
			AnchorPointDirection.TOP_RIGHT -> {
				element.position.bottomX = snapTo.x
				element.position.posY = snapTo.y
			}
			AnchorPointDirection.BOTTOM_RIGHT -> {
				element.position.bottomX = snapTo.x
				element.position.bottomY = snapTo.y
			}
			AnchorPointDirection.BOTTOM_LEFT -> {
				element.position.posX = snapTo.x
				element.position.bottomY = snapTo.y
			}
		}
	}
}
