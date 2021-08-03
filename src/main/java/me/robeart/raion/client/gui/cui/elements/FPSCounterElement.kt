package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.Raion
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.font.Fonts
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec2f

/**
 * @author cookiedragon234 17/Jun/2020
 */
class FPSCounterElement: CuiElement() {
	override fun render(mousePos: Vec2f) {
		super.render(mousePos)
		val fps = Minecraft.getDebugFPS()
		val text = "${ChatFormatting.GRAY}FPS ${ChatFormatting.WHITE}$fps"
		drawText(text, Utils.getRgb(255,255,255,255))
	}
}
