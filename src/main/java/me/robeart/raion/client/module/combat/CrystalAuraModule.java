package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent;
import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.FilterIterator;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.util.Utils;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.util.minecraft.RenderUtils;
import me.robeart.raion.client.value.*;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CrystalAuraModule extends Module {
	public static CrystalAuraModule INSTANCE;

	public ListValue priority = new ListValue("Priority", "Health", Arrays.asList("Health", "Distance", "Damage"));
	public BooleanValue place = new BooleanValue("Place", true);
	public IntValue placeDelay = new IntValue("Place Delay", 50, 0, 1000, 1, place);
	public DoubleValue rangep = new DoubleValue("Place Range", 5.8, 1, 10, 0.1, place);
	public BooleanValue checkLast = new BooleanValue("CheckLast", true, place);
	public BooleanValue explode = new BooleanValue("Hit", true);
	public IntValue hitDelay = new IntValue("Hit Delay", 50, 0, 1000, 1, explode);
	public DoubleValue rangeh = new DoubleValue("Hit Range", 5.5, 1, 10, 0.1, explode);
	public IntValue hitNumberSetting = new IntValue("Hits Before Switch", 5, 0, 30, 1, this.explode);
	public BooleanValue lethal = new BooleanValue("Lethal Mode", true);
	public IntValue lethalDamage = new IntValue("Lethal Health", 2, 1, 20, 1, lethal);
	public IntValue lethalMinDamage = new IntValue("Lethal Min Damage", 2, 1, 20, 1, lethal);
	public IntValue lethalPlace = new IntValue("Place Delay", 25, 0, 500, 1, lethal);
	public IntValue lethalHit = new IntValue("Hit Delay", 25, 0, 500, 1, lethal);
	public BooleanValue rotate = new BooleanValue("No Rotate", true);
	public BooleanValue autoSwitch = new BooleanValue("Auto Switch", true);
	public BooleanValue offhand = new BooleanValue("Offhand", false, autoSwitch);
	public IntValue offhandhp = new IntValue("Offhand HP", 20, 1, 36, 1, autoSwitch);
	public IntValue minDamage = new IntValue("Min Damage", 2, 1, 20, 1);
	public BooleanValue renderb = new BooleanValue("Render", true);
	public ColorValue color = new ColorValue("Color", Color.RED, renderb);
	public BooleanValue players = new BooleanValue("Player", true);
	public BooleanValue animals = new BooleanValue("Animals", false);
	public BooleanValue mobs = new BooleanValue("Mobs", false);
	public BooleanValue friends = new BooleanValue("Friends", false);
	public BooleanValue walls = new BooleanValue("Walls", true);
	public BooleanValue renderDamage = new BooleanValue("Render Damage", true);

	private boolean placeNew;
	private PlaceInfo placed;
	private PlaceInfo render;
	public EntityLivingBase target;
	private EntityEnderCrystal lastHit;
	private int hitNumber;
	private PlaceInfo toPlace;

	//cats' stuff
	private Timer hitTimer = new Timer();
	private Timer placeTimer = new Timer();
	//private BlockPos placePos;

	private Map<BlockPos, Double> damageMap;

	public CrystalAuraModule() {
		super("CrystalAura", "Automatically places crystals around your target", Category.COMBAT);
		INSTANCE = this;
	}

	public static float calculateDamage(BlockPos blockPos, Entity entity) {
		return calculateDamage((blockPos.getX() + .5), blockPos.up().getY(), (blockPos.getZ() + .5), entity);
	}

	/**
	 * calculates the damage done by an end crystal explosion at this spot
	 *
	 * @param posX   the x coordinate of the spot
	 * @param posY   the y coordinate of the spot
	 * @param posZ   the z coordinate of the spot
	 * @param entity the entity being damaged
	 * @return the damage result
	 */
	public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
		float doubleExplosionSize = 6.0F * 2.0F;
		double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
		Vec3d vec3d = new Vec3d(posX, posY, posZ);
		double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
		double v = (1.0D - distancedsize) * blockDensity;
		float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
		if (entity instanceof EntityLivingBase) {
			return getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
		}
		return damage;
	}

	/**
	 * gets the damage done by an EnderCrystal after blast reduction
	 *
	 * @param entity    the entity being damaged
	 * @param damage    the damage done to the entity
	 * @param explosion the explosion dealing the damage
	 * @return the damage after blast reduction
	 */
	public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) entity;
			DamageSource ds = DamageSource.causeExplosionDamage(explosion);
			damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
				.getAttributeValue());

			int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
			float f = MathHelper.clamp(k, 0.0F, 20.0F);
			damage = damage * (1.0F - f / 25.0F);

			if (entity.isPotionActive(Potion.getPotionById(11))) {
				damage = damage - (damage / 4);
			}
			return damage;
		}
		damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
			.getAttributeValue());
		return damage;
	}

	/**
	 * Gets the damage with the difficulty multiplier
	 *
	 * @param damage the damage to calculate with the multiplier
	 * @return the multiplied damage
	 */
	private static float getDamageMultiplied(float damage) {
		int diff = mc.world.getDifficulty().getId();
		return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
	}

	/**
	 * calculates the damage done by an end crystal to an entity
	 *
	 * @param crystal the end crystal dealing damage
	 * @param entity  the entity being damaged
	 * @return the damage done
	 */
	public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
		return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
	}

	@Override
	public synchronized void moduleLogic() {
		ArrayList<EntityLivingBase> newTargetList = new ArrayList<>();
		//Here we get a list of targets, and we mess with the end crystals
		if (mc.world == null || mc.player == null) return;
		for (Entity entity : mc.world.loadedEntityList.toArray(new Entity[0])) {
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase e = (EntityLivingBase) entity;
				if (mc.player.getDistance(e) > 15) continue;
				if (e instanceof EntityPlayerSP) continue;
				if (e.getHealth() <= 0 || e.isDead) continue;
				if (!animals.getValue() && (MinecraftUtils.isPassive(e) || MinecraftUtils.isNeutralMob(e))) continue;
				if (!mobs.getValue() && MinecraftUtils.isMobAggressive(e)) continue;
				if (players.getValue() && e instanceof EntityPlayer && !friends.getValue() && Raion.INSTANCE.getFriendManager()
					.isFriend((EntityPlayer) e))
					continue;
				newTargetList.add(e);
			}
		}
		if (priority.getValue().equalsIgnoreCase("Damage")) {
			PlaceInfo currentPlace = null;
			double damage = -1;
			EntityLivingBase currentTarget = null;
			for (EntityLivingBase target : newTargetList) {
				PlaceInfo bestPlace = bestToPlace(canCrystalBlocks(target), target);
				float thisDamage = calculateDamage(bestPlace.pos, target);
				if (currentPlace == null || bestPlace.damage > currentPlace.damage) {
					currentPlace = bestPlace;
					currentTarget = target;
					damage = thisDamage;
				}
				else if (bestPlace.damage == currentPlace.damage) {
					if (damage == -1 || thisDamage > damage) {
						damage = thisDamage;
						currentPlace = bestPlace;
						currentTarget = target;
					}
				}
			}
			this.target = currentTarget;
			this.toPlace = currentPlace;
		}
		else {
			target = getTarget(newTargetList);
			if (this.target != null) toPlace = bestToPlace(canCrystalBlocks(target), target);
		}
	}

	@Listener
	public synchronized void onWalkingPlayerUpdate(UpdateWalkingPlayerEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (BlinkModule.INSTANCE.getState()) return;
		try {
			for (Entity entity : mc.world.loadedEntityList) {
				//This doesn't check if there is a player nearby, but tbh I don't like that mechanic all that much
				//if anyone wants, I can look for a way to reimplement it
				if (entity instanceof EntityEnderCrystal) {
					if (this.hitTimer.passed(getHitDelay(target)) && explode.getValue()) {
						final EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
						if (mc.player.getDistance(crystal) <= this.rangeh.getValue()) {
							if (!walls.getValue() && !mc.player.canEntityBeSeen(crystal) && !MinecraftUtils.canEntityFeetBeSeen(crystal))
								continue;
							if (hitNumber >= hitNumberSetting.getValue() && lastHit == crystal) {
								placeNew = true;
								break;
							}
							this.breakCrystal(crystal);
							if (lastHit == crystal) hitNumber++;
							else {
								lastHit = crystal;
								hitNumber = 0;
							}
							this.hitTimer.reset();
							break;
						}
					}
				}
			}
			if (!this.place.getValue() || target == null) {
				this.render = null;
				return;
			}
			if (this.placeTimer.passed(this.getPlaceDelay(target))) {
				PlaceInfo placed = this.placed;
				if (checkLast.getValue() && placed != null) {
					final EntityEnderCrystal last = getCrystal(placed.pos);
					final boolean destroyed = last == null;
					if (!destroyed && !placeNew) {
						if (mc.player.getDistance(last) >= this.rangeh.getValue()) placeNew = true;
						this.render = null;
						return;
					}
					placeNew = false;
				}
				this.render = toPlace;
				placeCrystal(toPlace);
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Listener
	private synchronized void onRender3D(Render3DEvent event) {
		try {
			PlaceInfo render = this.render;
			if (renderb.getValue() && render != null && render.pos != null) {
				int c = Utils.getRgb(color.getValue().getRed(), color.getValue().getGreen(), color.getValue()
					.getBlue(), color.getValue().getAlpha());
				RenderUtils.blockEsp(render.pos, c, 1, 1);
			}

			Map<BlockPos, Double> damageMap = this.damageMap;
			if (damageMap != null) {
				for (Map.Entry<BlockPos, Double> entry : damageMap.entrySet()) {

				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * gets the hit delay for a specified target taking the lethal setting into account
	 *
	 * @param target the target to check for hit delay
	 * @return the hit delay for the target
	 */
	private int getHitDelay(EntityLivingBase target) {
		if (target == null || !lethal.getValue()) return hitDelay.getValue();
		if (target.getHealth() <= lethalDamage.getValue()) {
			return lethalHit.getValue();
		}
		else {
			return hitDelay.getValue();
		}

	}

	/**
	 * gets the place delay taking into account the lethal setting
	 *
	 * @param target the target to check
	 * @return the place delay for specified target
	 */
	private int getPlaceDelay(EntityLivingBase target) {
		if (this.lethal.getValue()) {
			if (target.getHealth() <= this.lethalDamage.getValue()) {
				return this.lethalPlace.getValue();
			}
		}
		return this.placeDelay.getValue();
	}

	/**
	 * gets the minimum dealt damage to a specified target
	 *
	 * @param target the target to check
	 * @return the minimum damage to said target
	 */
	private int getDamage(EntityLivingBase target) {
		if (lethal.getValue()) {
			if (target.getHealth() <= lethalDamage.getValue())
				return lethalMinDamage.getValue();
		}
		return minDamage.getValue();
	}

	/**
	 * gets a crystal at a given BlockPos
	 *
	 * @param pos the BlockPos to check for crystals
	 * @return the crystal at the given spot
	 */
	private EntityEnderCrystal getCrystal(BlockPos pos) {
		if (pos == null) return null;
		List<Entity> entitys = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0)));
		for (Entity entity : entitys) {
			if (entity instanceof EntityEnderCrystal) return (EntityEnderCrystal) entity;
		}
		return null;
	}

	/**
	 * finds the spot that is best to place at to deal the most damage to the target
	 *
	 * @param blocks the list of possible blocks to place
	 * @param target the target that will be damaged
	 * @return the best spot
	 */
	private PlaceInfo bestToPlace(Iterator<BlockPos.MutableBlockPos> blocks, EntityLivingBase target) {
		BlockPos.PooledMutableBlockPos bestPlace = BlockPos.PooledMutableBlockPos.retain();
		double crystalDamage = getDamage(target);
		while (blocks.hasNext()) {
			BlockPos.MutableBlockPos blockPos = blocks.next();
			double damageEnemy = calculateDamage(blockPos, target);
			//double damageSelf = calculateDamage(blockPos.getX(), blockPos.up().getY(), blockPos.getZ(), mc.player);
			//if ((damageSelf > damageEnemy && !(damageEnemy < ((EntityLivingBase) target).getHealth())) || damageEnemy - .5 > mc.player.getHealth()) continue;
			if (damageEnemy > crystalDamage) {
				crystalDamage = damageEnemy;
				bestPlace.setPos(blockPos);
			}
		}
		BlockPos out = new BlockPos(bestPlace);
		bestPlace.release();
		return new PlaceInfo(out, crystalDamage);
	}

	/**
	 * gets a list of blocks that you can place a crystal on
	 *
	 * @param target the entity to check around
	 * @return the list of blocks that you can place a crystal on
	 */
	private Iterator<BlockPos.MutableBlockPos> canCrystalBlocks(Entity target) {
		return new FilterIterator<BlockPos.MutableBlockPos>(MinecraftUtils.getBlocksInRadiusMutable(10, 10, target).iterator(), (block) -> {
			if (mc.player.getDistanceSqToCenter(block) > (rangep.getValue() * rangep.getValue())) return false;
			return canPlaceCrystal(block);
		});
	}

	/**
	 * checks if it is possible to place a crystal at the specified spot
	 *
	 * @param pos the spot
	 * @return the result
	 */
	private boolean canPlaceCrystal(BlockPos pos) {
		Block block = mc.world.getBlockState(pos).getBlock();
		if ((block == Blocks.OBSIDIAN) || (block == Blocks.BEDROCK)) {
			Block block1 = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
			Block block2 = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();
			return ((block1 == Blocks.AIR) && (block2 == Blocks.AIR) && mc.world.checkNoEntityCollision(new AxisAlignedBB(pos
				.up())));
		}
		return false;
	}

	/**
	 * gets a list of available targets from the loaded entities
	 *
	 * @return the list of possible targets
	 */
	private List<EntityLivingBase> getTargets() {
		ArrayList<EntityLivingBase> targetlist = new ArrayList<>();
		for (Entity en : mc.world.loadedEntityList) {
			if (!(en instanceof EntityLivingBase)) continue;
			EntityLivingBase e = (EntityLivingBase) en;
			if (mc.player.getDistance(e) > 15) continue;
			if (e == mc.player) continue;
			if (!walls.getValue() && !mc.player.canEntityBeSeen(e) && !MinecraftUtils.canEntityFeetBeSeen(e)) continue;
			if (e.getHealth() <= 0 || e.isDead) continue;
			if (!animals.getValue() && (MinecraftUtils.isPassive(e) || MinecraftUtils.isNeutralMob(e))) continue;
			if (!mobs.getValue() && MinecraftUtils.isMobAggressive(e)) continue;
			if (players.getValue() && e instanceof EntityPlayer && !friends.getValue() && Raion.INSTANCE.getFriendManager()
				.isFriend((EntityPlayer) e))
				continue;
			targetlist.add(e);
		}
		return targetlist;
	}

	//Instead of using this, I'm going to check the list only once and get 2 lists, one of targets and of crystals

	/**
	 * Gets the preferred target from a list of possible targets
	 *
	 * @param targets the list of targets to take from
	 * @return the preferred target
	 */
	private EntityLivingBase getTarget(List<EntityLivingBase> targets) {
		EntityLivingBase target = null;
		float distance = -1;
		float health = -1;
		boolean prioDistance = priority.getValue().equalsIgnoreCase("Distance");
		boolean prioHealth = priority.getValue().equalsIgnoreCase("Health");
		for (EntityLivingBase entity : targets) {
			if (prioDistance) {
				if (distance == -1 || mc.player.getDistance(entity) < distance) {
					distance = mc.player.getDistance(entity);
					target = entity;
				}
			}
			else if (prioHealth) {
				if (health == -1 || entity.getHealth() < health) {
					health = entity.getHealth();
					target = entity;
				}
			}
			else {
				// Prio damage
			}
		}
		return target;
	}

	/**
	 * looks at a pos, pretty nice and simple
	 *
	 * @param px the x pos to look at
	 * @param py the y pos to look at
	 * @param pz the z pos to look at
	 */
	private void lookAtPacket(double px, double py, double pz) {
		if (!this.rotate.getValue()) MinecraftUtils.lookAt(px, py, pz);
	}

	private void placeCrystal(PlaceInfo info) {
		if (info == null || info.pos == null) return;
		BlockPos pos = info.pos;
		EnumHand hand = autoSwitch();
		if (hand == null) {
			this.render = null;
			return;
		}
		RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player
			.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5));
		EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;

		Vec3d hitVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
		//.add(new Vec3d(facing.getOpposite().getDirectionVec()).scale(0.5));

		lookAtPacket(pos.getX() + .5, pos.getY() - .5, pos.getZ() + .5);
		if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing.getOpposite(), hitVec, hand) != EnumActionResult.FAIL) {
			mc.player.swingArm(hand);
			this.placed = info;
			this.hitNumber = 0;
			this.placeTimer.reset();
		}
		//mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(toPlace, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
	}

	private EnumHand autoSwitch() {
		Item mainhand = mc.player.getHeldItemMainhand().getItem();
		Item offhand = mc.player.getHeldItemOffhand().getItem();
		if (mainhand == Items.END_CRYSTAL) return EnumHand.MAIN_HAND;
		if (offhand == Items.END_CRYSTAL) return EnumHand.OFF_HAND;
		if (autoSwitch.getValue()) {
			if (this.offhand.getValue()) {
				boolean hpcheck = mc.player.getHealth() + mc.player.getAbsorptionAmount() > offhandhp.getValue();
				if (hpcheck) {
					int slot = MinecraftUtils.getSlotOfItem(Items.END_CRYSTAL);
					mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
					mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
					mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
					mc.playerController.updateController();
					return EnumHand.OFF_HAND;
				}
				else return null;
			}
			else {
				if (MinecraftUtils.holdItem(Items.END_CRYSTAL)) return EnumHand.MAIN_HAND;
				else return null;
			}
		}
		return null;
	}

	/**
	 * @param crystal the crystal input to break
	 * @author cats
	 * breaks a crystal for you, made to keep the code a bit neater for this
	 */
	private void breakCrystal(EntityEnderCrystal crystal) {
		this.lookAtPacket(crystal.posX, crystal.posY, crystal.posX);
		mc.playerController.attackEntity(mc.player, crystal);
		mc.player.swingArm(EnumHand.MAIN_HAND);
	}

	class PlaceInfo {
		BlockPos pos;
		double damage;

		PlaceInfo(BlockPos pos, double damage) {
			this.pos = pos;
			this.damage = damage;
		}
	}
}
