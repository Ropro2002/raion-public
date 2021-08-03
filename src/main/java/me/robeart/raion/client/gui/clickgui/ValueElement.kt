package me.robeart.raion.client.gui.clickgui

import me.robeart.raion.client.gui.clickgui.theme.RaionTheme
import me.robeart.raion.client.module.ClickGuiModule.colourSpeed
import me.robeart.raion.client.util.Interpolation
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.util.minecraft.GLUtils.getColor
import me.robeart.raion.client.value.*
import net.minecraft.client.Minecraft

/**
 * Holds and renders a value
 *
 * @author cookiedragon234 15/Jun/2020
 */
class ValueElement(
	val button: Button,
	val value: Value<*>,
	val children: List<ValueElement>,
	val theme: RaionTheme,
	val font: MinecraftFontRenderer
) {
	private val mc = Minecraft.getMinecraft()
	var settingsY = 0f
	var settingsopen = false
	
	var mouseDown = false
	var x: Float = 0f
	var y: Float = 0f
	var width: Float = 10f
	var height: Float = 10f
	
	fun draw(mouseX: Float, mouseY: Float) {
		val categoryPanel = button.categoryPanel
		val hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && GLUtils.isHovered(x, y, button.categoryPanel.width, 11f, mouseX, mouseY)
		
		if (hovering) {
			theme.tooltipDesc = value.description
		}
		
		val rightMinus = if (children.isNotEmpty()) 2f else 1f
		GLUtils.drawRect(x + 1, y, button.categoryPanel.width - rightMinus, 11f, getColour(hovering))
		GLUtils.drawRect(x, y - 1, 1f, 12f, getColor(60f))
		if (children.isNotEmpty()) {
			GLUtils.drawRect(x + button.categoryPanel.width - 1, y, 1f, 11f, getColor(60f))
		}
		if (value is BooleanValue) {
			val alpha = value.alphaOverlay.toFloat()
			if (alpha != 0f) {
				GLUtils.drawRect(x + 1, y, button.categoryPanel.width - rightMinus, 11f, getColor(alpha))
			}
		} else if (value is ListValue) {
			font.drawStringRightClamped(
				value.value,
				x,
				y + (11 - font.getStringHeight(value.value)) / 2,
				Utils.getRgb(167, 167, 178, 255),
				button.categoryPanel.width - 3
			)
		} else if (value is IntValue || value is DoubleValue || value is FloatValue) {
			var width = 0f
			var text: String? = "Error"
			when (value) {
				is IntValue -> {
					width = (button.categoryPanel.width / 100f * value.percentage)
					text = value.value.toString()
				}
				is DoubleValue -> {
					width = (button.categoryPanel.width / 100f * value.percentage)
					text = MathUtils.round(value.value, 2).toString()
				}
				is FloatValue -> {
					width = (button.categoryPanel.width / 100f * value.percentage)
					text = MathUtils.round(value.value, 3).toString()
				}
			}
			GLUtils.drawRect(x, y, width, 11f, getColor(40f))
			font.drawStringRightClamped(
				text,
				x,
				y + (11 - font.getStringHeight(text)) / 2,
				Utils.getRgb(167, 167, 178, 255),
				button.categoryPanel.width - 3
			)
			if (mouseDown) {
				val percentage = (if (mouseX < x) 0f else if (mouseX > x + button.categoryPanel.width - 2) 100f else (mouseX - x) / button.categoryPanel.width * 100f)
				when (value) {
					is IntValue -> {
						value.percentage = percentage
						val parent = value.parentSetting
						if (parent is ColorValue) {
							parent.setColor()
						}
					}
					is DoubleValue -> value.percentage = percentage
					is FloatValue -> value.percentage = percentage
				}
			}
		}
		font.drawStringClamped(
			value.name,
			x + 3,
			y + (11 - font.getStringHeight(value.name)) / 2,
			-0x33000001,
			width * 0.7f
		)
		if (settingsopen) {
			for (child in children) {
				if (value is ListValue) {
					val childValue = child.value
					if (childValue.listFilter != value.value) {
						// Filter does not match
						continue
					}
				}
				child.draw(mouseX, mouseY)
			}
		}
	}
	
	fun mouseClicked(mouseX: Float, mouseY: Float, state: Int) {
		val categoryPanel: CategoryPanel = button.categoryPanel
		val hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && GLUtils.isHovered(x, y, button.categoryPanel.width, 11f, mouseX, mouseY)
		if (hovering && button.settingsopen) {
			if (state == 0) {
				if (value is BooleanValue) value.value = value.value.not()
				else if (value is ListValue) value.nextValue()
				mouseDown = true
			}
			if (state == 1) {
				this.settingsopen = this.settingsopen.not()
			}
		}
	}
	
	fun mouseReleased(mouseX: Float, mouseY: Float, state: Int) {
		if (mouseDown && state == 0) {
			mouseDown = false
		} else {
			if (settingsopen) {
				// TODO
			}
		}
	}
	
	private var colour = 0
	fun getColour(hovered: Boolean): Int {
		val desired: Int
		desired = if (hovered) {
			Utils.getRgb(77, 77, 77, 100)
		} else {
			Utils.getRgb(0, 0, 0, 80)
		}
		colour = Interpolation.cinterpTo(colour, desired, mc.renderPartialTicks, colourSpeed)
		return colour
	}
}
