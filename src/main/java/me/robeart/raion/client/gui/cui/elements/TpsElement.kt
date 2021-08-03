package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.MathUtils.clamp
import me.robeart.raion.client.util.Utils
import net.minecraft.network.play.server.SPacketTimeUpdate
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f

/**
 * @author cookiedragon234 02/Jul/2020
 */
class TpsElement: CuiElement() {

	private val tps = FloatArray(20) { 20f }
	private var lastUpdate: Long = -1L
	private var next: Int = 0

	override fun render(mousePos: Vec2f) {
		super.render(mousePos)

		val tps = getTps()
		val text = "${ChatFormatting.GRAY}TPS ${ChatFormatting.WHITE}$tps"
		drawText(text)
	}

	override fun onPacketReceive(event: PacketReceiveEvent) {
		if(event.packet is SPacketTimeUpdate) {
			val now = System.currentTimeMillis()
			if (lastUpdate > 0) {
				val delta = (now - lastUpdate) / 1000f // delta seconds
				tps[next % tps.size] = clamp(20f / delta, 0f, 20f)
				next += 1
			}
			lastUpdate = now
		}
	}

	private fun getTps(): Float = if (tps.isNotEmpty()) tps.sum() / tps.size else 0f

}
