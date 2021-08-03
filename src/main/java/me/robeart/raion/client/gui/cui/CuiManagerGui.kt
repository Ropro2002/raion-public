package me.robeart.raion.client.gui.cui

import me.robeart.raion.client.events.events.render.Render2DEvent
import me.robeart.raion.client.module.ClickGuiModule.backgroundOpacity
import me.robeart.raion.client.module.ClickGuiModule.backgroundStyle
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.Utils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * @author cookiedragon234 15/Jun/2020
 */
object CuiManagerGui: GuiScreen() {
	/**
	 * This is the gui screen that was previously active before showing this gui screen
	 * It will be reinstated upon this gui being closed
	 */
	var parent: GuiScreen? = null
	
	var tooltipText: String? = null
	
	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		GlStateManager.pushMatrix()
		val scale = 1f //ClickGuiModule.INSTANCE.getScale();
		val sX = mouseX.toFloat() * scale
		val sY = mouseY.toFloat() * scale
		if (mc.world == null) {
			drawDefaultBackground() // Dirt background
		} else {
			val mode = backgroundStyle
			when (mode) {
				"Opaque" -> {
					val opacity = (backgroundOpacity * 255).toInt()
					//int start = Utils.getRgb(63, 239, 239, opacity); // 240 a
					//int end = Utils.getRgb(63, 239, 239, opacity);
					//this.drawGradientRect(0, 0, this.width, this.height, start, end);
					val start = Color(-1072689136)
					val end = Color(-804253680)
					val startI = Utils.getRgb(
						start.red,
						start.green,
						start.blue,
						opacity
					)
					val endI = Utils.getRgb(
						end.red,
						end.green,
						end.blue,
						opacity
					)
					drawGradientRect(0, 0, width, height, startI, endI)
				}
				"Blur"   -> if (!shaderLoaded) {
					loadShader()
				}
			}
			if (mode != "Blur" && shaderLoaded) {
				mc.entityRenderer.stopUseShader()
				shaderLoaded = false
			}
		}
		super.drawScreen(sX.toInt(), sY.toInt(), partialTicks)
		RaionCui.onRender2D()
		GlStateManager.popMatrix()
	}
	
	override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
		RaionCui.onMouseClick(mouseX.toFloat(), mouseY.toFloat(), mouseButton)
	}
	
	override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
		RaionCui.onMouseRelease(mouseX.toFloat(), mouseY.toFloat(), state)
	}
	
	override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
		RaionCui.onMouseMove(mouseX.toFloat(), mouseY.toFloat(), clickedMouseButton)
	}
	
	
	private var shaderLoaded = false
	private fun loadShader() {
		@Suppress("SENSELESS_COMPARISON")
		if (OpenGlHelper.shadersSupported && mc.entityRenderer.shaderGroup == null) {
			mc.entityRenderer.loadShader(ResourceLocation("minecraft", "shaders/post/blur.json"))
			shaderLoaded = true
		}
	}
	private var prevHideGui = false
	override fun initGui() {
		prevHideGui = mc.gameSettings.hideGUI
		mc.gameSettings.hideGUI = true
		if (mc.world != null) {
			val mode = backgroundStyle
			if (mode == "Blur") {
				loadShader()
			}
		}
	}
	override fun onGuiClosed() {
		mc.gameSettings.hideGUI = prevHideGui
		if (shaderLoaded) {
			mc.entityRenderer.stopUseShader()
			shaderLoaded = false
		}
	}
	
	
	override fun keyTyped(typedChar: Char, keyCode: Int) {
		if (keyCode == 1) {
			mc.displayGuiScreen(parent)
			parent = null
			if (mc.currentScreen == null) {
				mc.setIngameFocus()
			}
		}
	}
	override fun doesGuiPauseGame(): Boolean = false
}
