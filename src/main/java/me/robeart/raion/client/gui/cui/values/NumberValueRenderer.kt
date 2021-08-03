package me.robeart.raion.client.gui.cui.values

import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.module.ClickGuiModule
import me.robeart.raion.client.util.Interpolation
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.MouseButton
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.value.*
import net.minecraft.util.math.Vec2f

/**
 * @author cookiedragon234 17/Jun/2020
 */
class NumberValueRenderer<T: Number>(value: NumberValue<T>, font: MinecraftFontRenderer): ValueRenderer<NumberValue<T>>(value, font) {
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
	
	var mouseDown = false
	
	override fun render(mousePos: Vec2f) {
		val hovering = mouseOver(mousePos)
		
		if (hovering) {
			CuiManagerGui.tooltipText = value.description
		}
		
		GLUtils.drawRect(position.posX, position.posY, position.sizeX, position.sizeY, getColour(hovering))
		
		val value = value
		var width = 0f
		var text: String? = "Error"
		if (value is IntValue) {
			width = (position.sizeX / 100f * value.percentage)
			text = value.value.toString()
		}
		if (value is DoubleValue) {
			width = (position.sizeX / 100f * value.percentage)
			text = MathUtils.round(value.value, 2).toString()
		}
		if (value is FloatValue) {
			width = (position.sizeX / 100f * value.percentage)
			text = MathUtils.round(value.value, 2).toString()
		}
		GLUtils.drawRect(position.posX, position.posY, width, position.sizeY, GLUtils.getColor(40f))
		font.drawStringRight(text, position.bottomX - 1, position.posY + 1, Utils.getRgb(167, 167, 178, 255))
		
		
		font.drawString(value.name, position.posX + 1, position.posY + 1, -0x33000001)
	}
	
	override fun onMouseDown(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		if (!consumed) {
			val hovering = mouseOver(mousePos)
			if (hovering) {
				if (button == MouseButton.LEFT) {
					mouseDown = true
					checkPercentage(mousePos)
					return true
				}
			}
		}
		return false
	}
	
	override fun onMouseRelease(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		if (mouseDown && button == MouseButton.LEFT) {
			mouseDown = false
			return true
		}
		return false
	}
	
	override fun onMouseMove(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		if (!consumed && mouseDown) {
			checkPercentage(mousePos)
			return true
		}
		return false
	}
	
	private fun checkPercentage(mousePos: Vec2f) {
		val percentage = if (mousePos.x < position.posX) 0f else if (mousePos.x > position.bottomX) 100f else (mousePos.x - position.posX) / position.sizeX * 100f
		if (value is IntValue) value.percentage = percentage
		if (value is DoubleValue) value.percentage = percentage
		if (value is FloatValue) value.percentage = percentage
	}
}
