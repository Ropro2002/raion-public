package me.robeart.raion.client.gui.cui.elements

import me.robeart.raion.client.Raion
import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.RaionCui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.module.render.HudModule
import me.robeart.raion.client.util.MouseButton
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.font.Fonts
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.util.plus
import net.minecraft.util.math.Vec2f
import java.awt.Color

/**
 * Singleton used to modify other elements
 *
 * @author cookiedragon234 03/May/2020
 */
object AvailableElementManager: CuiElement() {
	init {
		this.width = 150f
		this.height = 50f
	}
	
	val titleFont = Fonts.font48
	
	var titleHeight = 0f
	var bodyHeight = 0f
	
	override fun render(mousePos: Vec2f) {
		this.width = 150f
		val elementTypes = RaionCui.availableElements
		val titleStr = "Available Elements"
		titleHeight = titleFont.getStringHeight(titleStr) + 4f
		this.height = titleHeight + bodyHeight
		val x = this.position.posX
		val y = this.position.posY
		val width = this.width.toFloat()
		
		super.render(mousePos)
		
		if (shouldRender()) {
			/**
			 * Comprises of two sections, title and body
			 * body contains a list of each available element
			 */
			
			var current = y // current height at which we are rendering
			GLUtils.drawRect(x, y, width, titleHeight + bodyHeight, Utils.getRgb(0, 0, 0, 70))
			GLUtils.drawRect(x + 1, y + 1, width - 2, titleHeight - 2, Utils.getRgb(0, 0, 0, 100))
			font.drawCenteredString(titleStr, x + (width / 2f), current + (titleHeight / 2) - 2f, Color.WHITE.rgb)
			current += titleHeight
			for ((name, element) in elementTypes) {
				if (element == AvailableElementManager::class.java || element == ActiveElementManager::class.java) continue
				
				val thisHeight = font.getStringHeight(name) + 4f
				GLUtils.drawRect(x + 1, current, width - 2, thisHeight - 2, Utils.getRgb(0, 0, 0, 100))
				font.drawStringRightClamped(name, x + width - 2f, current + 1f, Color.WHITE.rgb, width - 4f)
				font.drawString("+", x + 2f, current + 1f, Color.WHITE.rgb)
				current += thisHeight - 1f
			}
			bodyHeight = current - titleHeight - y
		}
	}
	
	override fun onMouseDown(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		return super.onMouseDown(mousePos, button, consumed).also {
			if (button == MouseButton.LEFT && it && shouldRender()) {
				var current = position.posY
				current += titleHeight
				for ((name, element) in RaionCui.availableElements) {
					if (element == AvailableElementManager::class.java || element == ActiveElementManager::class.java) continue
					
					val thisHeight = font.getStringHeight(name) + 4f
					if (
						mousePos.x >= x.toFloat()
						&&
						mousePos.x <= x.toFloat() + width.toFloat()
						&&
						mousePos.y >= current
						&&
						mousePos.y <= current + thisHeight
					) {
						val newInstance = element.newInstance()
						RaionCui.elements += newInstance
						return true
					}
					current += thisHeight -1f
				}
			}
		}
	}
	
	override fun shouldRender(): Boolean = mc.currentScreen == CuiManagerGui
}
