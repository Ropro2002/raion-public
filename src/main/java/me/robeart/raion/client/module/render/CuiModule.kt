package me.robeart.raion.client.module.render

import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent
import me.robeart.raion.client.events.events.render.Render2DEvent
import me.robeart.raion.client.gui.cui.RaionCui
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.network.play.server.SPacketTimeUpdate
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.awt.Color
import java.util.*

/**
 * @author cookiedragon234 16/Jun/2020
 */
object CuiModule: Module("Cui", "Hud", Category.RENDER) {

	val color = ListValue("Color", "Static", Arrays.asList("Static", "Rainbow"))
	val delayrainbow by ValueDelegate(IntValue("Rainbow Delay", 10, 1, 50, 1, color, "Rainbow"))
	val saturation by ValueDelegate(FloatValue("Saturation", 1f, 0.1f, 1f, 0.1f, color, "Rainbow"))
	val lightness by ValueDelegate(FloatValue("Lightness", 1f, 0.1f, 1f, 0.1f, color, "Rainbow"))
	val red by ValueDelegate(IntValue("Red", 255, 0, 255, 1, color, "Static"))
	val green by ValueDelegate(IntValue("Green", 100, 0, 255, 1, color, "Static"))
	val blue by ValueDelegate(IntValue("Blue", 255, 0, 255, 1, color, "Static"))

	@Listener
	fun onRender2D(event: Render2DEvent) {
		if (mc.currentScreen == null) {
			RaionCui.onRender2D()
		}
	}
	
	@Listener
	fun onUpdate(event: UpdateWalkingPlayerEvent) {
		RaionCui.onUpdate()
	}

	@Listener
	private fun onPacketReceive(event: PacketReceiveEvent) {
		if (event.stage != EventStageable.EventStage.PRE) return
		RaionCui.onPacketReceive(event)
	}

	fun getColor(): Int {
		if(color.value.equals("Static", ignoreCase = true)) {
			return Utils.getRgb(red, green, blue, 255)
		} else {
			val rainBowColor = System.currentTimeMillis() / delayrainbow % 360
			return Color.getHSBColor((rainBowColor / 360f), saturation, lightness).rgb
		}
	}


}
