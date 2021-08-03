package me.robeart.raion.client.module.combat

import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.network.PacketSendEvent
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent
import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.imixin.ICPacketPlayerTryUseItemOnBlock
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.immutable
import me.robeart.raion.client.util.isZero
import me.robeart.raion.client.util.minecraft.MinecraftUtils
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.client.util.mutableBlockPos
import me.robeart.raion.client.util.syncCurrentPlayItem
import me.robeart.raion.client.value.*
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemFood
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Explosion
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.awt.Color
import java.util.*
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * @author Robeart 8/06/2020
 */
object CrystalAura2: Module("CrystalAura2", "Crystal aura but version2", Category.COMBAT) {
	
	//General
	val priority by ValueDelegate(ListValue("Priority", "Health", listOf("Health", "Distance")))
	val minDamage by ValueDelegate(IntValue("Min Damage", 10, 1, 20, 1))
	val weakness by ValueDelegate(BooleanValue("Anti Weakness", true))
	val dubbletick by ValueDelegate(BooleanValue("Multitask", "Hit and Place in same tick", true))
	
	//Lethal
	val lethalS = BooleanValue("Lethal Mode", false)
	val lethal by ValueDelegate(lethalS)
	val lethalDamage by ValueDelegate(IntValue("Lethal Health", 10, 1, 20, 1, lethalS))
	val lethalMinDamage by ValueDelegate(IntValue("Lethal Min Damage", 2, 1, 20, 1, lethalS))
	val lethalPlace by ValueDelegate(IntValue("Place Delay", 0, 0, 20, 1, lethalS))
	val lethalHit by ValueDelegate(IntValue("Place Delay", 0, 0, 20, 1, lethalS))
	
	//Autoswitch
	val autoswitch by ValueDelegate(BooleanValue("Autoswitch", true))
	val offhand by ValueDelegate(BooleanValue("Offhand", false))
	
	//Hit
	val hitS = BooleanValue("Hit", true)
	val hit by ValueDelegate(hitS)
	val hitRange by ValueDelegate(DoubleValue("Hit Range", 4.25, 1.0, 8.0, 0.05, hitS))
	val hitSwitch by ValueDelegate(IntValue("Hit to switch", 20, 1, 50, 1, hitS))
	val hitDelay by ValueDelegate(IntValue("Hit Delay", 5, 0, 20, 1, hitS))
	val randomHit by ValueDelegate(IntValue("Random hit delay", 1, 0, 10, 1, hitS))
	val rotateHit by ValueDelegate(BooleanValue("Rotate", false, hitS))
	
	// Walls
	val wallRange by ValueDelegate(DoubleValue("Wall Range", 4.25, 1.0, 8.0, 0.05, hitS))
	val checkDamage by ValueDelegate(BooleanValue("Check Damage", false, hitS))
	val walls by ValueDelegate(BooleanValue("Walls", true, hitS))
	
	//Place
	val placeS = BooleanValue("Place", true)
	val place by ValueDelegate(placeS)
	val checkLast by ValueDelegate(BooleanValue("Check Last", true, placeS))
	val placeRange by ValueDelegate(DoubleValue("Place Range", 4.25, 1.0, 8.0, 0.05, placeS))
	val placeDelay by ValueDelegate(IntValue("Place Delay", 5, 0, 20, 1, placeS))
	val randomPlace by ValueDelegate(IntValue("Random place delay", 1, 0, 10, 1, placeS))
	val rotatePlace by ValueDelegate(BooleanValue("Rotate", false, placeS))
	
	//Wich entity's to hit
	val players by ValueDelegate(BooleanValue("Players", true))
	val monsters by ValueDelegate(BooleanValue("Monsters", true))
	val animals by ValueDelegate(BooleanValue("Animals", true))
	val friends by ValueDelegate(BooleanValue("Friends", true))
	
	//Rendering
	val renderS = BooleanValue("Render", true)
	val render by ValueDelegate(renderS)
	val up by ValueDelegate(BooleanValue("Up", false, renderS))
	val height by ValueDelegate(DoubleValue("Height", 1.0, 0.1, 1.0, 0.1, renderS))
	val color by ValueDelegate(ColorValue("Color", Color.RED, renderS))
	
	//Multithread
	val multithreadS = BooleanValue("MultiThread", true)
	val multiThread by ValueDelegate(multithreadS)
	val threadDelay by ValueDelegate(IntValue("Delay", 0, 0, 20, 1, multithreadS))
	
	val eating by ValueDelegate(BooleanValue("Eating Pause", true))
	
	val predictMovement by ValueDelegate(BooleanValue("Predict Movement", true))
	
	private var target: EntityLivingBase? = null
	private var toPlace: PlaceInfo? = null
	private var placed: PlaceInfo? = null
	private var renderPos: PlaceInfo? = null
	private var lastHit: EntityEnderCrystal? = null
	private var toHit: EntityEnderCrystal? = null
	private var hitTimes = 0
	private var placeNew = true
	
	private var threadTicks = 20
	private var hitTicks = 20
	private var placeTicks = 20
	
	override fun moduleLogic() {
		if (multiThread) {
			if (threadTicks >= threadDelay) {
				threadTicks = 0
				val target = if (hit && hitTicks >= getHitDelay(target)) getTargetPlayerAndPos() else null
				this.target = target
				//Do not calculate place if there is no target
				toPlace = null
				if (target != null && place && placeTicks >= getPlaceDelay(target)) {
					toPlace = bestToPlace(target)
				}
			} else threadTicks++
		}
	}
	
	@Listener
	fun onPacketSend(event: PacketSendEvent) {
		val packet = event.packet
		if (packet is CPacketPlayerTryUseItemOnBlock) {
			if (mc.player.getHeldItem(packet.hand).item == Items.END_CRYSTAL && packet.pos.y >= mc.world.height - 1 && packet.direction == EnumFacing.UP) {
				(packet as ICPacketPlayerTryUseItemOnBlock).setDirection(EnumFacing.DOWN)
			}
		}
	}
	
	@Listener
	fun onWalkingPlayerUpdate(event: UpdateWalkingPlayerEvent) {
		if (event.stage != EventStageable.EventStage.PRE) return
		if(mc.player.activeItemStack.item is ItemFood && eating) return
		
		val originalItem = mc.player.inventory.currentItem
		try {
			//Handle hit part
			if (hit && hitTicks >= getHitDelay(target)) {
				if (!multiThread) {
					this.target = getTargetPlayerAndPos()
				}
				this.toHit?.let { toHit ->
					if(weakness && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
						val strength = mc.player.getActivePotionEffect(MobEffects.STRENGTH)
						if(strength == null || strength.amplifier == 0) {
							if(!MinecraftUtils.holdItem(Items.DIAMOND)) return
						}
					}
					breakCrystal(toHit)
					if (lastHit == toHit) {
						hitTimes++
					} else {
						lastHit = toHit
						hitTimes = 1
					}
				}
				this.toHit = null
				if(!dubbletick) {
					placeTicks++
					return
				}
			}
			
			//Dont place if target is null
			this.target.let {
				if (it == null) {
					renderPos = null
					placed = null
					hitTimes = 0
					placeTicks = 0
				} else {
					//handle place part
					if (place && placeTicks >= getPlaceDelay(it)) {
						if (!multiThread) {
							toPlace = bestToPlace(it)
						}
						toPlace?.let { toPlace ->
							this.toPlace = null
							//Check if last crystal has been broken
							if (checkLast) {
								placed?.let { placed ->
									if (mc.player.getDistanceSq(placed.pos) >= hitRange.square()) {
										placeNew = true
									}
									if (getCrystal(placed.pos) == null) {
										renderPos = null
										return
									}
								}
							}
							
							//placeCrystal
							if (placeCrystal(toPlace)) {
								renderPos = toPlace
								placed = toPlace
								hitTimes = 0
								placeTicks = 0
							} else {
								renderPos = null
							}
						}
					} else placeTicks++
				}
			}
		} finally {
			if(autoswitch && LazyItemSwitch.state) mc.player.inventory.currentItem = originalItem
		}
	}
	
	@Listener
	fun onRender3D(event: Render3DEvent) {
		val renderPos = this.renderPos
		if (render && renderPos != null) {
			val pos = renderPos.pos
			RenderUtils.blockEspHeight(if (up) pos.up() else pos, color.rgb, height)
		}
	}
	
	/**
	 * Prepare crystal aura get Target, EndCrystal and Place position
	 */
	private fun getTargetPlayerAndPos(): EntityLivingBase? {
		//List of targets
		val targetList = ArrayList<EntityLivingBase>()
		//Check if the world is loaded
		if (mc.world == null || mc.player == null) {
			return null
		}
		//Get all possible targets
		
		for (e in mc.world.loadedEntityList.toTypedArray()) { // Need to copy into an array for concurrency issues
			if (e is EntityLivingBase) {
				val posX = if (predictMovement) e.posX + (e.motionX * TICKS_TO_PREDICT) else e.posX
				val posY = if (predictMovement) e.posY + (e.motionY * TICKS_TO_PREDICT) else e.posY
				val posZ = if (predictMovement) e.posZ + (e.motionZ * TICKS_TO_PREDICT) else e.posZ
				
				if (shouldAttack(e, posX, posY, posZ)) targetList.add(e)
			}
			if (e is EntityEnderCrystal) {
				val distanceSq = mc.player.getDistanceSq(e)
				
				
				
				if(!mc.player.canEntityBeSeen(e) && !MinecraftUtils.canEntityFeetBeSeen(e)) {
					if (!walls || distanceSq > wallRange.square()) continue
				} else if (distanceSq > hitRange.square()) continue
				
				if(checkDamage) {
					val target = this.target ?: continue
					
					val movementOffset = if (predictMovement)
						Vec3d(target.motionX * TICKS_TO_PREDICT, target.motionY * TICKS_TO_PREDICT, target.motionZ * TICKS_TO_PREDICT)
					else Vec3d.ZERO
					
					val offsetBB = if (movementOffset.isZero) target.entityBoundingBox else target.entityBoundingBox.offset(movementOffset)
					
					val posX = target.posX + movementOffset.x
					val posY = target.posY + movementOffset.y
					val posZ = target.posZ + movementOffset.z
					
					val d = calculateDamage(
						e.posX + .5,
						e.posY + 1.0,
						e.posZ + .5,
						target,
						posX,
						posY,
						posZ,
						offsetBB
					)
					if(d < getMinDamage(target)) continue
				}
				
				if (hitTimes >= hitSwitch && lastHit == e) {
					placeNew = true
					continue
				}
				toHit = e
				break
				
			}
		}
		//Get the best target
		return getTarget(targetList)
	}
	
	/**
	 * Places a endcrystal at given place
	 * @param place the PlaceInfo of where to place
	 * @return if place was succesfull
	 */
	private fun placeCrystal(place: PlaceInfo): Boolean {
		val pos = place.pos
		val hand = autoSwitch() ?: return false
		val result = mc.world.rayTraceBlocks(
			Vec3d(
				mc.player.posX, mc.player.posY + mc.player
					.getEyeHeight(), mc.player.posZ
			), Vec3d(pos.x + .5, pos.y + .5, pos.z + .5)
		)
		val facing = if (result == null || result.sideHit == null) EnumFacing.UP else result.sideHit
		
		val hitVec = Vec3d(pos).add(0.5, 0.5, 0.5)
		
		if(rotatePlace) MinecraftUtils.lookAt(pos.x + .5, pos.y - .5, pos.z + .5)
		if (mc.playerController.processRightClickBlock(
				mc.player,
				mc.world,
				pos,
				facing.opposite,
				hitVec,
				hand
			) != EnumActionResult.FAIL
		) {
			mc.player.swingArm(hand)
			return true
		}
		return false
	}
	
	/**
	 * breaks a crystal for you, made to keep the code a bit neater for this
	 * @param crystal the crystal input to break
	 */
	private fun breakCrystal(crystal: EntityEnderCrystal) {
		mc.playerController.syncCurrentPlayItem()
		if(rotateHit) MinecraftUtils.lookAt(crystal.posX, crystal.posY + crystal.eyeHeight, crystal.posX)
		mc.player.connection.sendPacket(CPacketUseEntity(crystal))
		mc.player.connection.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))
	}
	
	/**
	 * Switches to a endcrystal
	 */
	private fun autoSwitch(): EnumHand? {
		val mainhand = mc.player.heldItemMainhand.item
		val offhand = mc.player.heldItemOffhand.item
		if (mainhand == Items.END_CRYSTAL) return EnumHand.MAIN_HAND
		if (offhand == Items.END_CRYSTAL) return EnumHand.OFF_HAND
		return if (autoswitch) {
			if (MinecraftUtils.holdItem(Items.END_CRYSTAL)) EnumHand.MAIN_HAND else null
		} else null
	}
	
	/**
	 * Gets the preferred target from a list of possible targets
	 * @param targetList the list of targets to take from
	 * @return the preferred target
	 */
	private fun getTarget(targetList: ArrayList<EntityLivingBase>): EntityLivingBase? {
		var target: EntityLivingBase? = null
		var distance: Double? = null
		var health: Float? = null
		for (e in targetList) {
			when (priority) {
				"Distance" -> {
					val thisDistance = mc.player.getDistanceSq(e)
					if (distance == null || thisDistance < distance) {
						distance = thisDistance
						target = e
					}
				}
				"Health" -> {
					if (health == null || e.health < health) {
						health = e.health
						target = e
					}
				}
				"Damage" -> {
					//TODO add damage check mode
					target = e
				}
			}
		}
		return target
	}
	
	// Dont place crystals beyond this distance
	val maxDistance = 15.0.square()
	
	/**
	 * checks if it should attack a entity
	 * @param e the entity to check
	 * @return if it should attack the entity
	 */
	private fun shouldAttack(e: Entity, posX: Double = e.posX, posY: Double = e.posY, posZ: Double = e.posZ): Boolean {
		if (e is EntityPlayerSP || !e.isEntityAlive || mc.player.getDistanceSq(posX, posY, posZ) > maxDistance) return false
		return (players && e is EntityPlayer && (!friends || !Raion.INSTANCE.friendManager.isFriend(e))) || (monsters && MinecraftUtils.isMobAggressive(e))
		|| (animals && (MinecraftUtils.isPassive(e) || MinecraftUtils.isNeutralMob(e)))
	}
	
	private inline fun fastGetBlockState(pos: BlockPos)
		= mc.world.getChunk(pos).getBlockState(pos)

	private inline fun Double.square() = this * this
	
	/**
	 * checks if it is possible to place a crystal at the specified spot
	 * @param pos the spot
	 * @return if you can
	 */
	private fun canPlaceCrystal(pos: BlockPos.PooledMutableBlockPos, rangeSq: Double): Boolean {
		if(mc.player.getDistanceSq(pos.x + .5, pos.y + .5, pos.z + .5) > rangeSq) return false
		val state = fastGetBlockState(pos)
		if (state.block == Blocks.OBSIDIAN || state.block == Blocks.BEDROCK) {
			pos.setPos(pos.x, pos.y + 1, pos.z)
			if (fastGetBlockState(pos).block != Blocks.AIR) {
				return false
			}
			return mc.world.checkNoEntityCollision(AxisAlignedBB(pos))
		}
		return false
	}
	
	private const val TICKS_TO_PREDICT = 2

	/**
	 * Gets the best position to place a block
	 * @return the best block to place a crystal
	 */
	private fun bestToPlace(target: EntityLivingBase): PlaceInfo? {
		var bestPlace: BlockPos? = null
		var damage: Double = getMinDamage(target).toDouble()
		
		val movementOffset = if (predictMovement)
			Vec3d(target.motionX * TICKS_TO_PREDICT, target.motionY * TICKS_TO_PREDICT, target.motionZ * TICKS_TO_PREDICT)
		else Vec3d.ZERO
		
		val offsetBB = if (movementOffset.isZero) target.entityBoundingBox else target.entityBoundingBox.offset(movementOffset)

		val negDistance = -8
		val posDistance = 8
		val negYDistance = -8
		val posYDistance = 8

		mutableBlockPos { currentPos ->
			for (x in negDistance..posDistance) {
				for (y in negYDistance..posYDistance) {
					for (z in negDistance..posDistance) {
						if ((mc.player.posY + y) < 0 || (mc.player.posY + y) >= 256) continue
						currentPos.setPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z)
						currentPos.setPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z)
						if (!canPlaceCrystal(currentPos, placeRange.square())) continue
						currentPos.setPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z)
						val d = calculateDamage(
							currentPos.x + .5,
							currentPos.y + 1.0,
							currentPos.z + .5,
							target,
							target.posX + movementOffset.x,
							target.posY + movementOffset.y,
							target.posZ + movementOffset.z,
							offsetBB
						)
						if (d > damage) {
							damage = d.toDouble()
							bestPlace = currentPos.immutable()
						}
					}
				}
			}
		}
		return bestPlace?.let { PlaceInfo(it, damage) }
	}
	
	/**
	 * calculates the damage done by an end crystal explosion at this spot
	 * @param posX the x coordinate of the spot
	 * @param posY the y coordinate of the spot
	 * @param posZ the z coordinate of the spot
	 * @param entity the entity being damaged
	 * @return the damage result
	 */
	fun calculateDamage(posX: Double, posY: Double, posZ: Double, entity: Entity, entityX: Double, entityY: Double, entityZ: Double, bb: AxisAlignedBB): Float {
		val doubleExplosionSize = 6.0 * 2.0
		val distanceSize = sqrt((entityX - posX).square() + (entityY - posY).square() + (entityZ - posZ).square()) / doubleExplosionSize
		val blockDensity = entity.world.getBlockDensity(Vec3d(posX, posY, posZ), bb)
		val v = (1.0 - distanceSize) * blockDensity
		val damage = ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0).toFloat()
		return if (entity is EntityLivingBase) {
			getBlastReduction(entity, getDamageMultiplied(damage), Explosion(mc.world, null, posX, posY, posZ, 6f, false, true))
		} else damage
	}
	
	/**
	 * gets the damage done by an EnderCrystal after blast reduction
	 * @param entity the entity being damaged
	 * @param damage the damage done to the entity
	 * @param explosion the explosion dealing the damage
	 * @return the damage after blast reduction
	 */
	fun getBlastReduction(entity: EntityLivingBase, damage: Float, explosion: Explosion?): Float {
		var damage = damage
		if (entity is EntityPlayer) {
			val ds = DamageSource.causeExplosionDamage(explosion)
			damage = CombatRules.getDamageAfterAbsorb(damage, entity.totalArmorValue.toFloat(), entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
					.attributeValue.toFloat())
			val k = EnchantmentHelper.getEnchantmentModifierDamage(entity.armorInventoryList, ds)
			val f = MathHelper.clamp(k.toFloat(), 0.0f, 20.0f)
			damage *= (1.0f - f / 25.0f)
			if (entity.isPotionActive(Potion.getPotionById(11))) {
				damage -= damage / 4
			}
			return damage
		}
		damage = CombatRules.getDamageAfterAbsorb(damage, entity.totalArmorValue.toFloat(), entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
				.attributeValue.toFloat())
		return damage
	}
	
	/**
	 * Gets the damage with the difficulty multiplier
	 * @param damage the damage to calculate with the multiplier
	 * @return the multiplied damage
	 */
	private fun getDamageMultiplied(damage: Float): Float {
		val diff = mc.world.difficulty.id
		return damage * when (diff) {
			0    -> 0f
			2    -> 1f
			1    -> 0.5f
			else -> 1.5f
		}
	}

	
	/**
	 * gets the minimum dealt damage to a specified target
	 * @param target the target to check
	 * @return the minimum damage to said target
	 */
	private fun getMinDamage(target: EntityLivingBase): Int {
		if (lethal && target.health <= lethalDamage) return lethalMinDamage
		return minDamage
	}
	
	/**
	 * gets the place delay taking into account the lethal setting
	 * @param target the target to check
	 * @return the place delay for specified target
	 */
	private fun getPlaceDelay(target: EntityLivingBase): Int {
		var delay = placeDelay
		if (lethal && target.health <= lethalDamage) delay = lethalPlace
		if(randomPlace != 0) delay += Random.nextInt(-randomPlace, randomPlace)
		return delay
	}
	
	/**
	 * gets the hit delay for a specified target taking the lethal setting into account
	 * @param target the target to check for hit delay
	 * @return the hit delay for the target
	 */
	private fun getHitDelay(target: EntityLivingBase?): Int {
		var delay = hitDelay
		if (target != null) {
			if (lethal && target.health <= lethalDamage) delay = lethalHit
		}
		if(randomHit != 0) delay += Random.nextInt(-randomHit, randomHit)
		return delay
	}
	
	/**
	 * gets a crystal at a given BlockPos
	 * @param pos the BlockPos to check for crystals
	 * @return the crystal at the given spot
	 */
	private fun getCrystal(pos: BlockPos?): EntityEnderCrystal? {
		if (pos == null) return null
		return mc.world.getEntitiesWithinAABB(EntityEnderCrystal::class.java, AxisAlignedBB(pos.add(0, 1, 0))).firstOrNull()
	}
	
	//Class that stores info about crystals that will be placed
	internal class PlaceInfo(var pos: BlockPos, var damage: Double)
	
}
