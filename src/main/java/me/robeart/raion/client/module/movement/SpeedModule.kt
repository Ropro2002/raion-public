package me.robeart.raion.client.module.movement

import me.robeart.raion.client.events.events.player.PushOutOfBlocksEvent
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.value.DoubleValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import kotlin.math.ceil

/**
 * @author cookiedragon234 30/Apr/2020
 */
object SpeedModule: Module("Speed", "Removes deceleration kek", Category.MOVEMENT) {
	val max by ValueDelegate(DoubleValue("Max", 2.0, 0.5, 3.0, 0.1))
	
	@Listener
	fun onWalkingUpdate(event: UpdateWalkingPlayerEvent) {
		mc.player.isSprinting = true
		//if (mc.player.collidedHorizontally) {
		//	mc.player.collidedHorizontally = false
		//}
	}
	
	@Listener
	private fun pushOutofBlock(event: PushOutOfBlocksEvent) {
		event.isCanceled = true
		pushOutOfBlocks(event.x, event.y, event.z)
	}
	
	private fun pushOutOfBlocks(x: Double, y: Double, z: Double): Boolean {
		return if (mc.player.noClip) {
			false
		} else {
			if (mc.player.motionX > 0) {
				mc.player.motionX += 0.10000000149011612
			} else if (mc.player.motionX < 0) {
				mc.player.motionX -= 0.10000000149011612
			}
			if (mc.player.motionZ > 0) {
				mc.player.motionZ += 0.10000000149011612
			} else if (mc.player.motionZ < 0) {
				mc.player.motionZ -= 0.10000000149011612
			}
			mc.player.motionX = MathHelper.clamp(mc.player.motionX, -max, max)
			mc.player.motionZ = MathHelper.clamp(mc.player.motionZ, -max, max)
			
			return false
			
			val blockpos = BlockPos(x, y, z)
			val d0 = x - blockpos.x.toDouble()
			val d1 = z - blockpos.z.toDouble()
			val entHeight = ceil(mc.player.height.toDouble()).toInt().coerceAtLeast(1)
			val inTranslucentBlock: Boolean = !isHeadspaceFree(blockpos, entHeight)
			if (inTranslucentBlock) {
				var i = -1
				var d2 = 9999.0
				if (isHeadspaceFree(blockpos.west(), entHeight) && d0 < d2) {
					d2 = d0
					i = 0
				}
				if (isHeadspaceFree(blockpos.east(), entHeight) && 1.0 - d0 < d2) {
					d2 = 1.0 - d0
					i = 1
				}
				if (isHeadspaceFree(blockpos.north(), entHeight) && d1 < d2) {
					d2 = d1
					i = 4
				}
				if (isHeadspaceFree(blockpos.south(), entHeight) && 1.0 - d1 < d2) {
					d2 = 1.0 - d1
					i = 5
				}
				val f = 0.1f
				if (i == 0) {
					mc.player.motionX *= -0.10000000149011612
				}
				if (i == 1) {
					mc.player.motionX *= 0.10000000149011612
				}
				if (i == 4) {
					mc.player.motionZ *= -0.10000000149011612
				}
				if (i == 5) {
					mc.player.motionZ *= 0.10000000149011612
				}
			}
			false
		}
	}
	
	private fun isHeadspaceFree(pos: BlockPos, height: Int): Boolean {
		for (y in 0 until height) {
			if (!isOpenBlockSpace(pos.add(0, y, 0))) return false
		}
		return true
	}
	
	private fun isOpenBlockSpace(pos: BlockPos): Boolean {
		val iblockstate: IBlockState = mc.world.getBlockState(pos)
		return !iblockstate.block.isNormalCube(iblockstate, mc.world, pos)
	}
}
