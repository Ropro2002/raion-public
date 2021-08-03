package me.robeart.raion.client.gui.cui.values

import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.module.ClickGuiModule
import me.robeart.raion.client.util.Interpolation
import me.robeart.raion.client.util.MouseButton
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.value.ListValue
import net.minecraft.util.math.Vec2f

/**
 * @author cookiedragon234 17/Jun/2020
 */
class ListValueRenderer(value: ListValue, font: MinecraftFontRenderer): ValueRenderer<ListValue>(value, font) {
	private var colour = 0
	private fun getColour(hovered: Boolean): Int {
		val desired = if (hovered) {
			Utils.getRgb(77, 77, 77, 100)
		} else {
			Utils.getRgb(0, 0, 0, 80)
		}
		colour = Interpolation.cinterpTo(colour, desired, mc.renderPartialTicks, ClickGuiModule.colourSpeed)
		return colour
	}
	
	override fun render(mousePos: Vec2f) {
		val hovering = mouseOver(mousePos)
		
		if (hovering) {
			CuiManagerGui.tooltipText = value.description
		}
		
		GLUtils.drawRect(position.posX, position.posY, position.sizeX, position.sizeY, getColour(hovering))
		
		font.drawStringRight(value.value, position.posX + position.sizeX - 1, position.posY + 1, Utils.getRgb(167, 167, 178, 255))
		
		font.drawString(value.name, position.posX + 1, position.posY + 1, -0x33000001)
	}
	
	override fun onMouseDown(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		if (!consumed) {
			val hovering = mouseOver(mousePos)
			if (hovering) {
				if (button == MouseButton.LEFT) {
					value.nextValue()
					return true
				} else if (button == MouseButton.RIGHT) {
					value.previousValue()
					return true
				}
			}
		}
		return false
	}
	
	override fun onMouseRelease(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		return false
	}
	
	override fun onMouseMove(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		return false
	}
}
