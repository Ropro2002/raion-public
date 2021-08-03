package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.Raion
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec2f
import java.util.Arrays
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author cookiedragon234 23/Jun/2020
 */
class SpeedElement: CuiElement() {
	val units by ValueDelegate(ListValue("Units", "b/h", listOf("b/s", "m/s", "km/h")))
	val includeY by ValueDelegate(BooleanValue("Include Y", false))
	
	override fun render(mousePos: Vec2f) {
		super.render(mousePos)
		
		val unitSpeed = MathUtils.round(when(units) {
			"b/s" -> speed
			"m/s" -> speed
			"km/h" -> speed * 3.6
			else -> speed // not gonna happen
		}, 3).toString()
		
		val text = "${ChatFormatting.GRAY}$units ${ChatFormatting.WHITE}$unitSpeed"
		drawText(text, Utils.getRgb(255,255,255,255))
	}
	
	var speed: Double = 0.0
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (shouldRender()) {
			var speedSqr = mc.player.motionX.pow(2) + mc.player.motionZ.pow(2)
			if (includeY) {
				speedSqr += mc.player.motionY.pow(2)
			}
			speed = sqrt(speedSqr)
		}
	}
}
