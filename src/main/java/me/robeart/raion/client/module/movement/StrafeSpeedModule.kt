package me.robeart.raion.client.module.movement

import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent
import me.robeart.raion.client.imixin.IMinecraft
import me.robeart.raion.client.imixin.ITimer
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.ChatUtils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.DoubleValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.SPacketPlayerPosLook
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * @author cookiedragon234 30/Mar/2020
 */
object StrafeSpeedModule: Module("StrafeSpeed", "Allows you to move much faster", Category.MOVEMENT) {
	private val lagBackTime by ValueDelegate(FloatValue("Lag Back Pause", "Time (s) to wait before activating after lagging back", 0f, 0f, 1f, 0.1f))
	private val onlyForward by ValueDelegate(BooleanValue("Only Forward", true))
	private val accelerate by ValueDelegate(BooleanValue("Accelerate", false))
	private val decelerate by ValueDelegate(BooleanValue("Decelerate", false))
	private val y by ValueDelegate(DoubleValue("Y", 0.405, 0.1, 1.0, 0.001))
	private val ground by ValueDelegate(DoubleValue("Ground", 0.219, 0.1, 1.0, 0.001))
	private val air by ValueDelegate(DoubleValue("Air", 1.001, 0.5, 1.5, 0.001))
	private val timer by ValueDelegate(FloatValue("Timer", 22f, 20f, 30f, 0.5f).setCallback {
		if (this.state) {
			((mc as IMinecraft).timer as ITimer).tickLength = 1000f / it
		}
	})

	override fun onEnable() {
		((mc as IMinecraft).timer as ITimer).tickLength = 1000f / timer
	}

	override fun onDisable() {
		((mc as IMinecraft).timer as ITimer).tickLength = 1000f / 20f
	}

	@Listener
	fun onWalkingUpdate(event: UpdateWalkingPlayerEvent) {
		if (event.stage != EventStageable.EventStage.PRE) return
		
		if (
			!decelerate
			&&
			!mc.player.movementInput.forwardKeyDown
			&&
			!mc.player.movementInput.rightKeyDown
			&&
			!mc.player.movementInput.backKeyDown
			&&
			!mc.player.movementInput.leftKeyDown
		) {
			mc.player.motionX = 0.0
			mc.player.motionZ = 0.0
			return
		}
		
		if (lagBackStart != -1L && System.currentTimeMillis() - lagBackStart < (lagBackTime * 1000)) {
			return
		}
		lagBackStart = -1L

		val forward = (!onlyForward && mc.player.moveForward != 0f) || mc.player.moveForward > 0
		
		if (forward || mc.player.moveStrafing != 0f) {
			mc.player.isSprinting = true
			if (mc.player.onGround) {
				mc.player.motionY = y
				val direction = getPlayerDirection().toDouble()
				mc.player.motionX = -(sin(direction) * ground)
				mc.player.motionZ = (cos(direction) * ground)
			} else {
				var currentSpeed = 0.26
				if (accelerate) {
					currentSpeed = sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ)
				}
				val direction = getPlayerDirection().toDouble()
				mc.player.motionX = -sin(direction) * air * currentSpeed
				mc.player.motionZ = cos(direction) * air * currentSpeed
			}
		}
	}
	
	private var lagBackStart = -1L // millis to wait
	@Listener
	fun onPacketRecieve(event: PacketReceiveEvent) {
		if (event.stage != EventStageable.EventStage.PRE) return
		val packet = event.packet
		if (packet is SPacketPlayerPosLook) {
			if (isSetBack(packet)) {
				ChatUtils.message("Lag Back")
				lagBackStart = System.currentTimeMillis()
			}
		}
	}

	private fun getPlayerDirection(): Float {
		var var1 = mc.player.rotationYaw
		if (mc.player.moveForward < 0.0f) {
			var1 += 180.0f
		}
		var forward = 1.0f
		if (mc.player.moveForward < 0.0f) {
			forward = -0.5f
		} else if (mc.player.moveForward > 0.0f) {
			forward = 0.5f
		}
		if (mc.player.moveStrafing > 0.0f) {
			var1 -= 90.0f * forward
		}
		if (mc.player.moveStrafing < 0.0f) {
			var1 += 90.0f * forward
		}
		var1 *= 0.017453292f
		return var1
	}
	
	fun isSetBack(packetIn: SPacketPlayerPosLook): Boolean {
		val player = mc.player ?: return false
		var x = packetIn.x
		var y = packetIn.y
		var z = packetIn.z
		var yaw = packetIn.yaw
		var pitch = packetIn.pitch
		
		if (packetIn.flags.contains(SPacketPlayerPosLook.EnumFlags.X)) { x += player.posX } else { return true }
		if (packetIn.flags.contains(SPacketPlayerPosLook.EnumFlags.Y)) { y += player.posY } else { return true }
		if (packetIn.flags.contains(SPacketPlayerPosLook.EnumFlags.Z)) { z += player.posZ } else { return true }
		if (packetIn.flags.contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) { pitch += player.rotationPitch }
		if (packetIn.flags.contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) { yaw += player.rotationYaw }
		
		if (x != player.posX || y != player.posY || z != player.posZ || yaw != player.rotationYaw || pitch != player.rotationPitch) {
			return true
		}
		
		return false
	}
}
