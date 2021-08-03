package me.robeart.raion.client.module.combat

import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.network.PacketSendEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.Timer
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityBoat
import net.minecraft.entity.item.EntityMinecart
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.util.HashSet

/**
 * @author cookiedragon234 09/Jun/2020
 */
object CriticalsModule: Module("Criticals", "Deal critical damage", Category.COMBAT) {
	//private val mode by ValueDelegate(ListValue("Mode", "packet", listOf("ncp", "packet")))
	private val delay by ValueDelegate(IntValue("Delay", 1000, 0, 5000, 200))
	
	val packetsToIgnore = HashSet<Packet<*>>()
	val timer = Timer()

	@Listener
	fun onPacketSend(event: PacketSendEvent) {
		if (event.stage != EventStageable.EventStage.PRE) return

		val packet = event.packet

		if (packetsToIgnore.remove(packet)) {
			return
		}

		fun sendPacket(packet: Packet<*>) {
			mc.connection?.let {
				packetsToIgnore.add(packet)
				it.sendPacket(packet)
			}
		}

		if (packet is CPacketUseEntity && packet.action == CPacketUseEntity.Action.ATTACK) {
			if (!mc.player.onGround || mc.player.isInLava || mc.player.isInWater || mc.player.isOnLadder || mc.player.ridingEntity != null) {
				return
			}

			if (delay > 0 && !timer.passed(delay)) {
				return
			}

			val entity = packet.getEntityFromWorld(mc.world) ?: return
			val pos = mc.player.positionVector

			if (entity is EntityBoat || entity is EntityMinecart) {
				for (i in 0 until 5) {
					sendPacket(CPacketAnimation())
					sendPacket(CPacketUseEntity(entity))
				}
			}

			if (entity is EntityLivingBase) {
				sendPacket(CPacketPlayer.Position(pos.x, pos.y + 0.11, pos.z, false))
				sendPacket(CPacketPlayer.Position(pos.x, pos.y + 0.1100013579, pos.z, false))
				sendPacket(CPacketPlayer.Position(pos.x, pos.y + 0.0000013579, pos.z, false))
				mc.player.onCriticalHit(entity)
				timer.reset()
			}
		}
	}
}
