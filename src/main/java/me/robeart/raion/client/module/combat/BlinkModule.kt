package me.robeart.raion.client.module.combat

import me.robeart.raion.client.events.EventStageable.EventStage.PRE
import me.robeart.raion.client.events.events.network.PacketSendEvent
import me.robeart.raion.client.module.Module
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author cookiedragon234 09/Jun/2020
 */
object BlinkModule: Module(
	"Blink",
	"Pauses sending packets, so it sends all packets at once when turned off",
	Category.COMBAT
) {
	private val packets = ConcurrentLinkedQueue<Packet<*>>()
	
	@Listener
	fun onSendPacket(event: PacketSendEvent) {
		if (event.stage != PRE) return
		
		if (mc.world == null || mc.player == null || mc.isSingleplayer) return
		
		val packet = event.packet
		if (packet is CPacketChatMessage
			|| packet is CPacketConfirmTeleport
			|| packet is CPacketKeepAlive
			|| packet is CPacketTabComplete
			|| packet is CPacketClientStatus
		) {
			return
		}
		packets.add(packet)
		event.isCanceled = true
	}
	
	override fun onEnable() {
		packets.clear()
	}
	
	override fun onDisable() {
		packets.forEach {
			mc.connection?.sendPacket(it)
		}
		packets.clear()
	}
}
