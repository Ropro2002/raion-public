package me.robeart.raion.client.gui.cui.elements

import me.robeart.raion.client.Raion
import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.RaionCui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.gui.cui.utils.Box2f
import me.robeart.raion.client.util.MouseButton
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.font.Fonts
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.util.minecraft.GLUtils
import net.minecraft.util.math.Vec2f
import java.awt.Color

/**
 * @author cookiedragon234 16/Jun/2020
 */
object ActiveElementManager: CuiElement() {
	init {
		this.width = 150f
		this.height = 50f
	}
	
	val buttons: ArrayList<RenderedElementButton> = arrayListOf()
	
	val titleFont = Fonts.font48
	
	var titleHeight = 0f
	var bodyHeight = 0f
	
	override fun render(mousePos: Vec2f) {
		this.width = 150f
		val elements = RaionCui.elements
		val titleStr = "Active Elements"
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
			buttons.trimToSize()
			val numElements = elements.count { it != AvailableElementManager && it != ActiveElementManager }
			val dirty = buttons.size != numElements
			if (dirty) {
				buttons.clear()
				buttons.ensureCapacity(numElements)
				for (element in elements) {
					if (element == AvailableElementManager || element == ActiveElementManager) continue
					
					val name = RaionCui.elementNameMap[element::class.java]!!
					buttons.add(RenderedElementButton(
						element,
						Box2f(),
						Box2f(),
						name
					))
				}
			}
			
			for (button in buttons) {
				val thisHeight = font.getStringHeight(button.name) + 4f
				val closeWidth = font.getStringWidth("X")
				
				button.position.posX = x
				button.position.posY = current - 1f
				button.position.sizeX = width
				button.position.sizeY = thisHeight
				
				button.removeBox.posX = x + width - closeWidth - 2f
				button.removeBox.posY = current
				button.removeBox.sizeX = closeWidth + 2f
				button.removeBox.sizeY = thisHeight
				
				GLUtils.drawRect(button.position.posX + 1, button.position.posY + 1, button.position.sizeX - 2, button.position.sizeY - 2, Utils.getRgb(0, 0, 0, 100))
				font.drawString(button.name, button.position.posX + 2, button.position.posY + 2, Color.WHITE.rgb)
				font.drawString("X", button.removeBox.posX, button.removeBox.posY + 2, Color.WHITE.rgb)
				current += thisHeight - 2f
				
				if (button.settingsOpen) {
					for ((value, renderer) in button.element.values) {
						val thisHeight = font.getStringHeight(value.name) + 4f
						renderer.position.posX = x + 2f
						renderer.position.posY = current + 1f
						renderer.position.sizeX = width - 3f
						renderer.position.sizeY = thisHeight - 1f
						renderer.render(mousePos)
						current += thisHeight
					}
				}
				current += 1f
			}
			bodyHeight = current - titleHeight - y
		}
	}
	
	override fun onMouseDown(mousePos: Vec2f, mouseBtn: MouseButton, consumed: Boolean): Boolean {
		var thisConsumed = consumed
		if (shouldRender()) {
			for (button in buttons) {
				if (!thisConsumed && mouseBtn == MouseButton.RIGHT && button.position.contains(mousePos)) {
					button.settingsOpen = button.settingsOpen.not()
					for ((value, renderer) in button.element.values) {
						renderer.visible = button.settingsOpen
					}
					thisConsumed = true
				}
				if (!thisConsumed && mouseBtn == MouseButton.LEFT && button.removeBox.contains(mousePos)) {
					RaionCui.elements.remove(button.element)
					thisConsumed = true
				}
				for ((value, renderer) in button.element.values) {
					if (renderer.onMouseDown(mousePos, mouseBtn, thisConsumed)) {
						thisConsumed = true
					}
				}
			}
		}
		thisConsumed = thisConsumed || super.onMouseDown(mousePos, mouseBtn, consumed)
		return thisConsumed
	}
	
	override fun onMouseMove(mousePos: Vec2f, mouseBtn: MouseButton, consumed: Boolean): Boolean {
		var thisConsumed = false
		if (shouldRender()) {
			for (button in buttons) {
				for ((value, renderer) in button.element.values) {
					if (renderer.onMouseMove(mousePos, mouseBtn, consumed || thisConsumed)) {
						thisConsumed = true
					}
				}
			}
		}
		thisConsumed = thisConsumed || super.onMouseMove(mousePos, mouseBtn, consumed)
		return thisConsumed
	}
	
	override fun onMouseRelease(mousePos: Vec2f, mouseBtn: MouseButton, consumed: Boolean): Boolean {
		var thisConsumed = false
		if (shouldRender()) {
			for (button in buttons) {
				for ((value, renderer) in button.element.values) {
					if (renderer.onMouseRelease(mousePos, mouseBtn, consumed || thisConsumed)) {
						thisConsumed = true
					}
				}
			}
		}
		thisConsumed = thisConsumed || super.onMouseRelease(mousePos, mouseBtn, consumed)
		return thisConsumed
	}
	
	data class RenderedElementButton(
		val element: CuiElement,
		val position: Box2f,
		val removeBox: Box2f,
		val name: String = RaionCui.elementNameMap[element::class.java]!!,
		var settingsOpen: Boolean = false
	)
	
	override fun shouldRender(): Boolean = mc.currentScreen == CuiManagerGui
}
