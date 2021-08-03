package me.robeart.raion.client.module.player

import me.robeart.raion.client.events.events.block.IsLiquidSolidEvent
import me.robeart.raion.client.events.events.client.ClickMouseButtonEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.MouseButton
import me.robeart.raion.client.value.BooleanValue
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 25/Mar/2020
 */
object LiquidInteractModule: Module(
	"LiquidInteract",
	"Makes liquids like water and lava react the same way as blocks",
	Category.PLAYER
) {
	private val onlyRightClick = BooleanValue("Only Right Click", true)
	private var solid = false
	
	@Listener
	private fun onMouseClick(event: ClickMouseButtonEvent) {
		if (event.mouseButton == MouseButton.RIGHT && onlyRightClick.value) {
			solid = true
			mc.entityRenderer.getMouseOver(1f)
		}
	}
	
	@Listener
	private fun onLiquidSolidityCheck(isLiquidSolidEvent: IsLiquidSolidEvent) {
		if (onlyRightClick.value) {
			isLiquidSolidEvent.solid = solid
			solid = false
		} else {
			isLiquidSolidEvent.solid = true
		}
	}
}
