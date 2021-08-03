package me.robeart.raion.client.module.player

import me.robeart.raion.client.events.EventStageable.EventStage.PRE
import me.robeart.raion.client.events.events.network.PacketSendEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 08/Jun/2020
 */
object InteractionTweaksModule: Module("InteractionTweaks", "Tweak interaction", Category.PLAYER) {
	val stickyBreak by ValueDelegate(BooleanValue("Sticky Break", true))
	val bypassWorldBorder by ValueDelegate(BooleanValue("Bypass World Border", true))
	val multiTask by ValueDelegate(BooleanValue("Multitask", true))
	
	private val placedBlockDirection by lazy {
		CPacketPlayerTryUseItemOnBlock::class.java.declaredFields.firstOrNull { it.type == EnumFacing::class.java }
			?.also {
				it.isAccessible = true
			}
	}
	
	@Listener
	fun onSendPacket(event: PacketSendEvent) {
		if (event.stage == PRE && bypassWorldBorder) {
			val packet = event.packet
			if (packet is CPacketPlayerTryUseItemOnBlock) {
				if (packet.pos.y >= 255 && packet.direction == EnumFacing.UP) {
					placedBlockDirection?.also { placedBlockDirection ->
						Utils.setFinalStatic(InteractionTweaksModule.placedBlockDirection, packet, EnumFacing.DOWN)
					} ?: println("Null field")
				} else if (!mc.world.worldBorder.contains(packet.pos)) {
					val newDir = when (packet.direction) {
						EnumFacing.EAST  -> EnumFacing.WEST
						EnumFacing.NORTH -> EnumFacing.SOUTH
						EnumFacing.WEST  -> EnumFacing.EAST
						EnumFacing.SOUTH -> EnumFacing.NORTH
						else             -> packet.direction
					}
					
					placedBlockDirection?.also { placedBlockDirection ->
						Utils.setFinalStatic(InteractionTweaksModule.placedBlockDirection, packet, newDir)
					} ?: println("Null field")
				}
			}
		}
	}
}
