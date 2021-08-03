package me.robeart.raion.client.module.render

import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.color
import me.robeart.raion.client.util.draw
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.client.util.vertex
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * @author cookiedragon234 18/Jun/2020
 */
object TrajectoriesModule: Module("Trajectories", "Show arrow trajectories", Category.RENDER) {
	val others by ValueDelegate(BooleanValue("Others", "Render other players trajectories", false))
	val distance by ValueDelegate(IntValue("Distance", "The number of ticks to simulate", 55, 10, 60, 5))
	val checkCollisions by ValueDelegate(BooleanValue("Collisions", "Stop upon a collision", true))
	
	@Listener
	fun onRender3d(event: Render3DEvent) {
		for (hand in EnumHand.values()) {
			val heldItem = mc.player.getHeldItem(hand).item
			if (heldItem != Items.BOW || mc.player.activeHand == hand) {
				if (isThrowable(heldItem)) drawTrajectory(mc.player, heldItem)
				break
			}
		}
		
		if (others) {
			for (player in mc.world.playerEntities) {
				if (player == mc.player || player == mc.renderViewEntity) continue
				
				for (hand in EnumHand.values()) {
					val heldItem = player.getHeldItem(hand).item
					if (heldItem != Items.BOW || player.activeHand == hand) {
						if (isThrowable(heldItem)) drawTrajectory(player, heldItem)
						break
					}
				}
			}
		}
	}
	
	fun drawTrajectory(entity: EntityLivingBase, item: Item) {
		val projectile = getProjectileFromItem(entity, item) ?: return
		
		GlStateManager.pushMatrix()
		RenderUtils.beginRender()
		GlStateManager.disableDepth()
		GlStateManager.depthMask(false)
		GlStateManager.shadeModel(GL11.GL_SMOOTH)
		GlStateManager.glLineWidth(2f)
		GlStateManager.disableTexture2D()
		GL11.glEnable(GL11.GL_LINE_SMOOTH)
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
		
		draw(translate = true, glMode = GL11.GL_LINE_STRIP, format = POSITION_COLOR) {
			for (i in 0..distance) {
				vertex {
					pos(projectile.posX, projectile.posY, projectile.posZ)
					color(Color.RED)
				}
				
				projectile.onTick()
				if (projectile.stopped) break // Projectile has hit an object
			}
		}
		
		GlStateManager.shadeModel(GL11.GL_FLAT)
		GL11.glDisable(GL11.GL_LINE_SMOOTH)
		GlStateManager.enableDepth()
		GlStateManager.depthMask(true)
		RenderUtils.endRender()
		GlStateManager.enableTexture2D()
		GlStateManager.popMatrix()
	}
	
	fun getProjectileFromItem(entity: EntityLivingBase, item: Item): SimulatedProjectile? {
		return when (item) {
			is ItemBow -> {
				val maxUseDuration = 72000
				val i = maxUseDuration - entity.itemInUseCount
				if (i < 0) return null // not enough power
				val f = ItemBow.getArrowVelocity(i)
				if (f < 0.1f) return null
				val critical = f == 1.0f
				val startVelocity = f * 3f
				
				SimulatedProjectile(gravity = 0.05000000074505806, startingMotion = 1.0, power = startVelocity, width = 0.5f, height = 0.5f)
			}
			is ItemEgg -> {
				SimulatedProjectile(pitchOffset = 0f, power = 1.5f)
			}
			is ItemEnderPearl -> {
				SimulatedProjectile(pitchOffset = 0f, power = 1.5f)
			}
			is ItemExpBottle -> {
				SimulatedProjectile(gravity = 0.07, startingMotion = 1.0, pitchOffset = -20f, power = 0.7f)
			}
			is ItemLingeringPotion -> {
				SimulatedProjectile(pitchOffset = -20f, power = 0.5f, gravity = 0.05)
			}
			is ItemSnowball -> {
				SimulatedProjectile(pitchOffset = 0f, power = 1.5f)
			}
			is ItemSplashPotion -> {
				SimulatedProjectile(pitchOffset = -20f, power = 0.5f, gravity = 0.05)
			}
			else -> {
				//error(item::class.java)
				//println(item::class.java)
				null
			}
		}
	}
	
	open class SimulatedProjectile(
		var shooter: Entity = mc.player,
		var posX: Double = shooter.lastTickPosX + (shooter.posX - shooter.lastTickPosX) * mc.renderPartialTicks,
		var posY: Double = shooter.lastTickPosY + (shooter.posY - shooter.lastTickPosY) * mc.renderPartialTicks + shooter.eyeHeight,
		var posZ: Double = shooter.lastTickPosZ + (shooter.posZ - shooter.lastTickPosZ) * mc.renderPartialTicks,
		var yaw: Float = shooter.rotationYaw,
		var pitch: Float = shooter.rotationPitch,
		var prevYaw: Float = yaw,
		var prevPitch: Float = pitch,
		var motionX: Double = 0.0,
		var motionY: Double = 0.0,
		var motionZ: Double = 0.0,
		var pitchOffset: Float = 0f,
		val width: Float = 0.25f,
		val height: Float = 0.25f,
		var gravity: Double = 0.03,
		var startingMotion: Double = 1.0,
		var power: Float = 1.5f
	) {
		val bb = kotlin.run {
			val d0 = width.toDouble() / 2.0
			AxisAlignedBB(posX - d0, posY, posZ - d0, posX + d0, posY + height.toDouble(), posZ + d0)
		}
		var stopped = false
		var inWater = false
		
		init {
			motionX = (-sin(Math.toRadians(yaw.toDouble())) * cos(Math.toRadians(pitch.toDouble())) * startingMotion)
			motionY = (-sin(Math.toRadians(pitch.toDouble() + pitchOffset)) * startingMotion)
			motionZ = (cos(Math.toRadians(yaw.toDouble())) * cos(Math.toRadians(pitch.toDouble())) * startingMotion)
			
			val f = sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ)
			
			motionX /= f
			motionY /= f
			motionZ /= f
			
			motionX *= power
			motionY *= power
			motionZ *= power
			
			motionX += shooter.motionX
			if (!shooter.onGround) {
				motionY += shooter.motionY
			}
			motionZ += shooter.motionZ
			
			val horizontalMotion = sqrt(motionX * motionX + motionZ * motionZ)
			this.yaw = Math.toRadians(MathHelper.atan2(motionX, motionZ)).toFloat()
			this.pitch = Math.toRadians(MathHelper.atan2(motionY, horizontalMotion)).toFloat()
			this.prevYaw = yaw
			this.prevPitch = pitch
		}
		
		open fun onTick() {
			if (stopped) return
			
			this.prevPitch = this.pitch
			this.prevYaw = this.yaw
			
			posX += motionX
			posY += motionY
			posZ += motionZ
			
			val blockPos = BlockPos(posX, posY, posZ)
			if (!mc.world.isBlockLoaded(blockPos)) {
				stopped = true
				return
			} else if (checkCollisions) {
				val state = mc.world.getBlockState(blockPos)
				if (state.material != Material.AIR) {
					val blockBB = state.getCollisionBoundingBox(mc.world, blockPos)
					if (blockBB != null && blockBB.offset(blockPos).contains(Vec3d(posX, posY, posZ))) {
						// Projectile is inside a block
						stopped = true
						return
					}
				}
				
				val entities = mc.world.getEntitiesWithinAABBExcludingEntity(shooter, bb)
				if (entities.isNotEmpty()) {
					// Collided with entity
					stopped = true
					return
				}
			}
			
			val f4 = sqrt(motionX * motionX + motionZ * motionZ)
			yaw = Math.toRadians(MathHelper.atan2(motionX, motionZ)).toFloat()
			
			this.pitch = Math.toRadians(MathHelper.atan2(motionY, f4)).toFloat()
			while (this.pitch - this.prevPitch < -180.0f) {
				this.prevPitch -= 360.0f
			}
			
			while (this.pitch - this.prevPitch >= 180.0f) {
				this.prevPitch += 360.0f
			}
			
			while (this.yaw - this.prevYaw < -180.0f) {
				this.prevYaw -= 360.0f
			}
			
			while (this.yaw - this.prevYaw >= 180.0f) {
				this.prevYaw += 360.0f
			}
			
			this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2f
			this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2f
			var f1 = 0.99
			val f2 = 0.05f
			
			if (this.isInWater()) {
				f1 = 0.6
			}
			motionX *= f1
			motionY *= f1
			motionZ *= f1
			
			motionY -= gravity
		}
		
		fun isInLava(): Boolean {
			return mc.world.isMaterialInBB(bb.grow(-0.10000000149011612, -0.4000000059604645, -0.10000000149011612), Material.LAVA)
		}
		fun isInWater(): Boolean {
			return mc.world.isMaterialInBB(bb.grow(-0.10000000149011612, -0.4000000059604645, -0.10000000149011612), Material.WATER)
		}
	}
	
	fun isThrowable(item: Item): Boolean =
		item is ItemBow || item is ItemExpBottle || item is ItemEnderPearl || item is ItemEgg || item is ItemSnowball || item is ItemSplashPotion || item is ItemLingeringPotion || item is ItemFishingRod
}
