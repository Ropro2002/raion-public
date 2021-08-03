package me.robeart.raion.client.module.movement

import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.entity.AddEntityVelocityEvent
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.imixin.ISPacketEntityVelocity
import me.robeart.raion.client.imixin.ISPacketExplosion
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.value.FloatValue
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketExplosion
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 23/Jun/2020
 */
object VelocityModule: Module(
	"Velocity",
	"Stops you from taking knockback",
	Category.MOVEMENT
) {
	private val motionX = FloatValue("X", 0f, 0f, 2f, 0.1f)
	private val motionY = FloatValue("Y", 0f, 0f, 2f, 0.1f)
	private val motionZ = FloatValue("Z", 0f, 0f, 2f, 0.1f)
	
	@Listener
	private fun onPacketReceive(event: PacketReceiveEvent) {
		if (event.stage != EventStageable.EventStage.PRE) return
		if (mc.player == null) return
		if (event.packet is SPacketEntityVelocity) {
			if ((event.packet as SPacketEntityVelocity).entityID == mc.player.entityId) {
				val packetEntityVelocity = event.packet as ISPacketEntityVelocity
				if (motionX.value == 0f && motionY.value == 0f && motionZ.value == 0f) {
					event.isCanceled = true
					return
				} else {
					packetEntityVelocity.setMotionX(mc.player.motionX.toInt())
					packetEntityVelocity.setMotionY(mc.player.motionY.toInt())
					packetEntityVelocity.setMotionZ(mc.player.motionZ.toInt())
				}
			}
		}
		if (event.packet is SPacketExplosion) {
			val packetExplosion = event.packet as ISPacketExplosion
			packetExplosion.setMotionX(motionX.value)
			packetExplosion.setMotionY(motionY.value)
			packetExplosion.setMotionZ(motionZ.value)
		}
	}
	
	@Listener
	private fun addEntityVelocity(event: AddEntityVelocityEvent) {
		if (event.entity == null || event.entity == mc.player) {
			event.x *= motionX.value.toDouble()
			event.y *= motionY.value.toDouble()
			event.z *= motionZ.value.toDouble()
		}
	}
}
