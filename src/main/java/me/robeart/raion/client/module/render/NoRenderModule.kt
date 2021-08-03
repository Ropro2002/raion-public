package me.robeart.raion.client.module.render

import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.events.events.render.HurtCameraEffectEvent
import me.robeart.raion.client.events.events.render.RenderOverlayEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.network.play.server.SPacketExplosion
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 08/Jun/2020
 */
object NoRenderModule: Module("NoRender", "Stops rendering things completely", Category.RENDER) {
	val fire by ValueDelegate(BooleanValue("Fire", true))
	val liquid by ValueDelegate(BooleanValue("Liquid", true))
	val explosion by ValueDelegate(BooleanValue("Explosion", true))
	val item by ValueDelegate(BooleanValue("Item In Hand", false))
	val noHurtCam by ValueDelegate(BooleanValue("No Hurtcam", true))

	@Listener
	private fun onRenderOverlay(event: RenderOverlayEvent) {
		if (fire && event.type == RenderOverlayEvent.OverlayType.FIRE) event.isCanceled = true
		if (liquid && event.type == RenderOverlayEvent.OverlayType.LIQUID) event.isCanceled = true
		if (item && event.type == RenderOverlayEvent.OverlayType.ITEM) event.isCanceled = true
	}

	@Listener
	fun onPacketReceive(event: PacketReceiveEvent) {
		if (explosion && event.packet is SPacketExplosion) {
			event.isCanceled = true
		}
	}

	@Listener
	fun onRenderHurtCam(event: HurtCameraEffectEvent) {
		if (noHurtCam) {
			event.isCanceled = true
		}
	}
}
