package me.robeart.raion.client.module.movement

import me.robeart.raion.client.events.events.player.MoveEvent
import me.robeart.raion.client.imixin.IEntity
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraftforge.client.event.InputUpdateEvent
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 23/Jun/2020
 */
object NoSlowDownModule: Module(
	"NoSlowDown",
	"Stops things like eating from slowing you down",
	Category.MOVEMENT
) {
	val items by ValueDelegate(BooleanValue("Items", true))
	val webs by ValueDelegate(BooleanValue("Webs", true))
	val liquids by ValueDelegate( BooleanValue("Liquids", false))
	val liquidsUp by ValueDelegate( BooleanValue("Liquids Up", false))
	val sidewaysMovement by ValueDelegate(BooleanValue("Sideways", true))
	
	@Listener
	fun onMove(event: MoveEvent?) {
		if (webs) {
			(mc.player as IEntity).setIsInWeb(false)
		}
	}
	
	@Listener
	fun onInput(event: InputUpdateEvent?) { // The event kami uses
		if (items) {
			// If the player is using their hand (Dont speed up while riding as movement isnt affected anyway)
			if (mc.player.isHandActive && !mc.player.isRiding) {
				mc.player.movementInput.moveStrafe /= 0.2f
				mc.player.movementInput.moveForward /= 0.2f
			}
		}
	}
}
