package me.robeart.raion.client.gui.cui.anchor

import me.robeart.raion.client.gui.cui.RaionCui
import me.robeart.raion.client.gui.cui.anchor.AnchorPointDirection.*
import me.robeart.raion.client.gui.cui.element.CuiElement
import java.util.UUID

/**
 * @author cookiedragon234 17/Jun/2020
 */
class ElementAnchorPoint(var snapTo: CuiElement?, direction: AnchorPointDirection, val uuid: UUID? = snapTo?.uuid): AnchorPoint(direction) {
	override fun snap(element: CuiElement) {
		val snapTo = snapTo ?: (RaionCui.elements.firstOrNull { it.uuid == uuid } ?: return)
		this.snapTo = snapTo
		when (direction) {
			TOP_LEFT -> {
				element.position.posX = snapTo.position.posX
				element.position.posY = snapTo.position.posY - element.position.sizeY
			}
			TOP_RIGHT -> {
				element.position.posX = snapTo.position.bottomX - element.position.sizeX
				element.position.posY = snapTo.position.posY - element.position.sizeY
			}
			BOTTOM_RIGHT -> {
				element.position.posX = snapTo.position.bottomX - element.position.sizeX
				element.position.posY = snapTo.position.bottomY
			}
			BOTTOM_LEFT -> {
				element.position.posX = snapTo.position.posX
				element.position.posY = snapTo.position.bottomY
			}
		}
	}
}
