package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.FloatValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.Arrays;

public class KillAuraModule extends Module {
	
	public ListValue mode = new ListValue("Mode", "Single", Arrays.asList("Single", "Multiple"));
	public ListValue priority = new ListValue("Priority", "Health", Arrays.asList("Health", "Distance"));
	private BooleanValue swingArm = new BooleanValue("Swing Arm", true);
	private BooleanValue swordSwitch = new BooleanValue("Switch", false);
	private BooleanValue shield = new BooleanValue("Shield", false);
	private BooleanValue wait = new BooleanValue("Delay", false);
	private BooleanValue players = new BooleanValue("Player", false);
	private BooleanValue animals = new BooleanValue("Animals", false);
	private BooleanValue mobs = new BooleanValue("Mobs", false);
	private BooleanValue shulkerBullet = new BooleanValue("Shulker Bullet", false);
	private BooleanValue boats = new BooleanValue("Boats", false);
	private BooleanValue friends = new BooleanValue("Friends", false);
	private BooleanValue walls = new BooleanValue("Walls", true);
	private BooleanValue rotate = new BooleanValue("Rotate", false);
	private FloatValue range = new FloatValue("Range", 5.5f, 1, 10, 0.1f);
	
	public KillAuraModule() {
		super("KillAura", "Automatically hits people", Category.COMBAT);
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		if (BlinkModule.INSTANCE.getState()) return;
		
		int previousSlot = mc.player.inventory.currentItem;
		try {
			if (mc.player.isDead) return;
			if (shield.getValue()
				&&
				(
					mc.player.isHandActive()
						&&
						mc.player.getActiveHand() == EnumHand.OFF_HAND
						&&
						mc.player.getHeldItemOffhand().getItem().equals(Items.SHIELD)
				)) return;
			if (mc.player.isHandActive() && mc.player.getActiveHand() == EnumHand.MAIN_HAND) return;
			if (wait.getValue()) {
				if (mc.player.getCooledAttackStrength(0) < 1) return;
				else if (mc.player.ticksExisted % 2 != 0) return;
			}
			boolean holdingSword = MinecraftUtils.isHoldingItem(Items.DIAMOND_SWORD);
			if (mode.getValue().equalsIgnoreCase("Single")) {
				Entity entity = getTarget(getTarget());
				if (entity == null) return;
				if (!holdingSword && swordSwitch.getValue()) {
					MinecraftUtils.holdItem(Items.DIAMOND_SWORD);
					holdingSword = true;
				}
				if (rotate.getValue()) {
					MinecraftUtils.lookAt(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
				}
				mc.playerController.attackEntity(mc.player, entity);
				if (swingArm.getValue()) {
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}
			}
			else {
				boolean attacked = false;
				for (Entity e : getTarget()) {
					if (e == null) continue;
					if (!holdingSword && swordSwitch.getValue()) {
						MinecraftUtils.holdItem(Items.DIAMOND_SWORD);
						holdingSword = true;
					}
					if (rotate.getValue()) {
						MinecraftUtils.lookAt(e.posX, e.posY + e.getEyeHeight(), e.posZ);
					}
					mc.playerController.attackEntity(mc.player, e);
					attacked = true;
				}
				if (attacked && swingArm.getValue()) {
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}
			}
		}
		finally {
			mc.player.inventory.currentItem = previousSlot;
		}
	}
	
	private ArrayList<Entity> getTarget() {
		ArrayList<Entity> targetList = new ArrayList<>();
		for (Entity e : mc.world.loadedEntityList) {
			if (e == null) continue;
			if (mc.player.getDistance(e) > range.getValue()) continue;
			if (e instanceof EntityPlayerSP) continue;
			if (!walls.getValue() && !mc.player.canEntityBeSeen(e) && !canEntityFeetBeSeen(e)) continue;
			if (boats.getValue() && e instanceof EntityBoat) {
				targetList.add(e);
				continue;
			}
			if (!(e instanceof EntityLivingBase)) continue;
			if (((EntityLivingBase) e).getHealth() <= 0 || e.isDead) continue;
			if (wait.getValue() && ((EntityLivingBase) e).hurtTime != 0) continue;
			if (!animals.getValue() && MinecraftUtils.isPassive(e)) continue;
			if (!mobs.getValue() && MinecraftUtils.isMobAggressive(e)) continue;
			if (players.getValue() && e instanceof EntityPlayer && !friends.getValue() && Raion.INSTANCE.getFriendManager()
				.isFriend((EntityPlayer) e))
				continue;
			targetList.add(e);
		}
		return targetList;
	}
	
	private boolean target(Entity e) {
		if (e == null) return false;
		if (mc.player.getDistance(e) > range.getValue()) return false;
		if (e == mc.player || e == mc.getRenderViewEntity()) return false;
		if (!walls.getValue() && !mc.player.canEntityBeSeen(e) && !canEntityFeetBeSeen(e)) return false;
		if (boats.getValue() && e instanceof EntityBoat) return true;
		if (e instanceof EntityShulkerBullet && shulkerBullet.getValue()) return true;
		if (!(e instanceof EntityLivingBase)) return false;
		if (((EntityLivingBase) e).getHealth() <= 0 || e.isDead) return false;
		if (wait.getValue() && ((EntityLivingBase) e).hurtTime != 0) return false;
		if (!animals.getValue() && MinecraftUtils.isPassive(e)) return false;
		return mobs.getValue() || !MinecraftUtils.isMobAggressive(e);
	}
	
	private Entity getTarget(ArrayList<Entity> targets) {
		Entity target = null;
		float distance = range.getValue();
		float health = 69;
		for (Entity entity : targets) {
			if (boats.getValue() && entity instanceof EntityBoat) {
				target = entity;
				break;
			}
			if (priority.getValue().equalsIgnoreCase("Distance")) {
				if (mc.player.getDistance(entity) < distance) {
					distance = mc.player.getDistance(entity);
					target = entity;
				}
			}
			else if (((EntityLivingBase) entity).getHealth() < health) {
				health = ((EntityLivingBase) entity).getHealth();
				target = entity;
			}
		}
		return target;
	}
	
	private boolean canEntityFeetBeSeen(Entity entityIn) {
		return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
	}
	
}
