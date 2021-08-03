package me.robeart.raion.client.managers

import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.events.events.network.PacketSendEvent
import me.robeart.raion.client.util.Interpolation
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.network.play.client.CPacketPlayer
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 09/Jun/2020
 */
object RotationManager {
	init {
		Raion.INSTANCE.eventManager.addEventListener(this)
	}
	
	private val mc = Minecraft.getMinecraft()
	
	val lastReportedYawF by lazy {
		EntityPlayerSP::class.java.declaredFields.first { it.name == "field_175164_bL" || it.name == "lastReportedYaw" }
			.also {
				it.isAccessible = true
			}
	}
	val lastReportedPitchF by lazy {
		EntityPlayerSP::class.java.declaredFields.first { it.name == "field_175165_bM" || it.name == "lastReportedPitch" }
			.also {
				it.isAccessible = true
			}
	}
	
	var lastReportedYaw: Float
		get() = lastReportedYawF.get(mc.player) as Float
		set(value) {
			lastReportedYawF.set(mc.player, value)
		}
	var lastReportedPitch: Float
		get() = lastReportedPitchF.get(mc.player) as Float
		set(value) {
			lastReportedPitchF.set(mc.player, value)
		}
	
	fun sendRotation(yaw: Float, pitch: Float, onGround: Boolean? = null) {
		if (Interpolation.isNearlyEqual(yaw, lastReportedYaw) && Interpolation.isNearlyEqual(pitch, lastReportedPitch)) {
			return
		}
		
		mc.player?.connection?.sendPacket(CPacketPlayer.Rotation(yaw, pitch, onGround ?: mc.player.onGround))
		
		lastReportedYaw = yaw
		lastReportedPitch = pitch
	}
	
	@Listener
	fun onPacket(event: PacketSendEvent) {
		if (event.stage != EventStageable.EventStage.PRE) return
		
		val packet = event.packet
		if (packet is CPacketPlayer) {
			val yaw = packet.getYaw(Float.NaN)
			val pitch = packet.getPitch(Float.NaN)
			if (!yaw.isNaN()) {
				if (Interpolation.isNearlyEqual(yaw, lastReportedYaw) && Interpolation.isNearlyEqual(pitch, lastReportedPitch)) {
					event.isCanceled = true
					return
				}
			}
		}
	}
}
