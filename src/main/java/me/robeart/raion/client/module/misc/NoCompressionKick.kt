package me.robeart.raion.client.module.misc

import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.network.PacketExceptionEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.ChatUtils
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

object NoCompressionKick: Module(
	"NoCompKick",
	"Stops the exception from being thrown for badly compressed packets",
	Category.MISC
) {
	@Listener
	fun onBadPacket(event: PacketExceptionEvent) {
		if (event.stage == EventStageable.EventStage.PRE) {
			event.isCanceled = true
			ChatUtils.message("Prevented packet exception from being thrown")
		} else if (event.stage == EventStageable.EventStage.POST) {
			event.isCanceled = true
			ChatUtils.message("Prevented thrown exception from disconnect")
		}
	}
}
