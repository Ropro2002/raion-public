package me.robeart.raion.client.events.events.client

import me.robeart.raion.client.events.EventCancellable
import me.robeart.raion.client.util.MouseButton

/**
 * @author cookiedragon234 25/Mar/2020
 */
data class ClickMouseButtonEvent(
	val mouseButton: MouseButton
): EventCancellable(EventStage.PRE)

