package me.robeart.raion.client.module.player

import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.events.render.RenderOverlayEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.isNearlyZero
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.DoubleValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.block.Block
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.event.world.WorldEvent
import org.lwjgl.input.Keyboard
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 30/Apr/2020
 */
object FreecamModule: Module("Freecam", "Control your camera separately to your body", Category.PLAYER) {
	private val follow by ValueDelegate(BooleanValue("Follow", false))
	private val copyInventory by ValueDelegate(BooleanValue("Copy Inventory", true))
	private val hSpeed by ValueDelegate(DoubleValue("H Speed", 1.0, 0.2, 2.0, 0.1))
	private val vSpeed by ValueDelegate(DoubleValue("V Speed", 1.0, 0.2, 2.0, 0.1))
	private var cachedActiveEntity: Entity? = null
	private var lastActiveTick: Int = -1
	
	private var oldRenderEntity: Entity? = null
	private var camera: FreecamCamera? = null
	private var cameraMovement: MovementInput = object: MovementInputFromOptions(mc.gameSettings) {
		override fun updatePlayerMoveState() {
			if (!Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
				super.updatePlayerMoveState()
			} else {
				this.moveStrafe = 0f
				this.moveForward = 0f
				this.forwardKeyDown = false
				this.backKeyDown = false
				this.leftKeyDown = false
				this.rightKeyDown = false
				this.jump = false
				this.sneak = false
			}
		}
	}
	private var playerMovement: MovementInput = object: MovementInputFromOptions(mc.gameSettings) {
		override fun updatePlayerMoveState() {
			if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
				super.updatePlayerMoveState()
			} else {
				this.moveStrafe = 0f
				this.moveForward = 0f
				this.forwardKeyDown = false
				this.backKeyDown = false
				this.leftKeyDown = false
				this.rightKeyDown = false
				this.jump = false
				this.sneak = false
			}
		}
	}
	
	fun getActiveEntity(): Entity? {
		if (cachedActiveEntity == null) {
			cachedActiveEntity = mc.player
		}
		
		val currentTick = Raion.tick
		if (lastActiveTick != currentTick) {
			lastActiveTick = currentTick
			
			cachedActiveEntity = if (this.state) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
					mc.player
				} else {
					mc.renderViewEntity ?: mc.player
				}
			} else {
				mc.player
			}
		}
		return cachedActiveEntity
	}
	
	@Listener
	fun onWorldLoad(event: WorldEvent.Unload) {
		mc.renderViewEntity = mc.player
		this.state = false
	}
	
	override fun onEnable() {
		if (mc.player == null) return
		
		camera = FreecamCamera().also { camera ->
			camera.movementInput = cameraMovement
			mc.player.movementInput = playerMovement
			mc.world.addEntityToWorld(-921, camera)
		}
		oldRenderEntity = mc.renderViewEntity
		mc.renderViewEntity = camera
		mc.renderChunksMany = false
		//mc.renderGlobal.loadRenderers()
	}
	
	override fun onDisable() {
		if (mc.player == null) return
		
		camera?.let { camera ->
			mc.world.removeEntity(camera)
		}
		camera = null
		mc.player.movementInput = MovementInputFromOptions(mc.gameSettings)
		mc.renderViewEntity = oldRenderEntity
		mc.renderChunksMany = true
		//mc.renderGlobal.loadRenderers()
	}
	
	@Listener
	private fun onRenderOverlay(event: RenderOverlayEvent) {
		if (event.type == RenderOverlayEvent.OverlayType.ITEM) event.isCanceled = true
	}
	
	private class FreecamCamera: EntityPlayerSP(
		mc,
		mc.world,
		mc.connection,
		mc.player.statFileWriter,
		mc.player.recipeBook
	) {
		init {
			this.noClip = true
			this.health = mc.player.health
			this.posX = mc.player.posX
			this.posY = mc.player.posY
			this.posZ = mc.player.posZ
			this.prevPosX = mc.player.prevPosX
			this.prevPosY = mc.player.prevPosY
			this.prevPosZ = mc.player.prevPosZ
			this.lastTickPosX = mc.player.lastTickPosX
			this.lastTickPosY = mc.player.lastTickPosY
			this.lastTickPosZ = mc.player.lastTickPosZ
			this.rotationYaw = mc.player.rotationYaw
			this.rotationPitch = mc.player.rotationPitch
			this.rotationYawHead = mc.player.rotationYawHead
			this.prevRotationYaw = mc.player.prevRotationYaw
			this.prevRotationPitch = mc.player.prevRotationPitch
			this.prevRotationYawHead = mc.player.prevRotationYawHead
			if (copyInventory) {
				this.inventory = mc.player.inventory
				this.inventoryContainer = mc.player.inventoryContainer
				this.setHeldItem(EnumHand.MAIN_HAND, mc.player.heldItemMainhand)
				this.setHeldItem(EnumHand.OFF_HAND, mc.player.heldItemOffhand)
			}
			this.capabilities.readCapabilitiesFromNBT(NBTTagCompound().also { nbt ->
				mc.player.capabilities.writeCapabilitiesToNBT(nbt)
			})
			this.capabilities.isFlying = true
			this.attackedAtYaw = mc.player.attackedAtYaw
			this.movementInput = MovementInputFromOptions(mc.gameSettings)
		}
		
		
		override fun writeEntityToNBT(compound: NBTTagCompound) {}
		override fun readEntityFromNBT(compound: NBTTagCompound) {}
		
		override fun isInsideOfMaterial(materialIn: Material): Boolean {
			return mc.player.isInsideOfMaterial(materialIn)
		}
		
		override fun getActivePotionMap(): MutableMap<Potion, PotionEffect> {
			return mc.player.activePotionMap
		}
		
		override fun getActivePotionEffects(): MutableCollection<PotionEffect> {
			return mc.player.activePotionEffects
		}
		
		override fun getTotalArmorValue(): Int {
			return mc.player.totalArmorValue
		}
		
		override fun getAbsorptionAmount(): Float {
			return mc.player.absorptionAmount
		}
		
		override fun isPotionActive(potionIn: Potion): Boolean {
			return mc.player.isPotionActive(potionIn)
		}
		
		override fun getActivePotionEffect(potionIn: Potion): PotionEffect? {
			return mc.player.getActivePotionEffect(potionIn)
		}
		
		override fun getFoodStats(): FoodStats {
			return mc.player.foodStats
		}
		
		override fun canTriggerWalking() = false
		override fun getCollisionBox(entityIn: Entity): AxisAlignedBB? = null
		override fun getCollisionBoundingBox(): AxisAlignedBB? = null
		override fun getEntityBoundingBox(): AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
		override fun canBePushed(): Boolean = false
		override fun applyEntityCollision(entityIn: Entity) {}
		override fun attackEntityFrom(source: DamageSource, amount: Float): Boolean = false
		override fun canBeAttackedWithItem(): Boolean = false
		override fun canBeCollidedWith(): Boolean = false
		override fun canBeRidden(entityIn: Entity): Boolean = false
		override fun canRenderOnFire(): Boolean = false
		override fun canTrample(world: World, block: Block, pos: BlockPos, fallDistance: Float): Boolean = false
		override fun doBlockCollisions() {}
		override fun updateFallState(y: Double, onGroundIn: Boolean, state: IBlockState, pos: BlockPos) {}
		override fun getIsInvulnerable(): Boolean = true
		override fun getPushReaction(): EnumPushReaction = EnumPushReaction.IGNORE
		override fun hasNoGravity(): Boolean = true
		
		override fun onLivingUpdate() {
			this.motionX = 0.0
			this.motionY = 0.0
			this.motionZ = 0.0
			this.movementInput.updatePlayerMoveState()
			val up = if (this.movementInput.jump) 1f else if (this.movementInput.sneak) -1f else 0f
			setMotion(this.movementInput.moveStrafe, up, this.movementInput.moveForward)
			if (Module.mc.gameSettings.keyBindSprint.isKeyDown) {
				this.motionX *= 2
				this.motionY *= 2
				this.motionZ *= 2
				this.isSprinting = true
			} else {
				this.isSprinting = false
			}
			if (follow) {
				if (this.motionX.isNearlyZero()) {
					this.posX += (mc.player.posX - mc.player.prevPosX)
				}
				if (this.motionY.isNearlyZero()) {
					this.motionY += (mc.player.posY - mc.player.prevPosY)
				}
				if (this.motionZ.isNearlyZero()) {
					this.motionZ += (mc.player.posZ - mc.player.prevPosZ)
				}
			}
			this.setPosition(posX + motionX, posY + motionY, posZ + motionZ)
		}
		
		fun setMotion(strafe: Float, up: Float, forward: Float) {
			var strafe = strafe
			var up = up
			var forward = forward
			var f = strafe * strafe + up * up + forward * forward
			if (f >= 1.0E-4f) {
				f = MathHelper.sqrt(f)
				if (f < 1.0f) f = 1.0f
				f /= 2f
				strafe *= f
				up *= f
				forward *= f
				
				val f1 = MathHelper.sin(rotationYaw * 0.017453292f)
				val f2 = MathHelper.cos(rotationYaw * 0.017453292f)
				motionX = (strafe * f2 - forward * f1).toDouble() * hSpeed
				motionY = up.toDouble() * vSpeed
				motionZ = (forward * f2 + strafe * f1).toDouble() * hSpeed
			}
		}
	}
}
// hc
