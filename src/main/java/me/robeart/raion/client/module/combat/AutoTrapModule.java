package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.DoubleValue;
import me.robeart.raion.client.value.IntValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Robeart
 */
public class AutoTrapModule extends Module {
	
	public ListValue mode = new ListValue("Mode", "Normal", Arrays.asList("Normal", "AntiStep"));
	public BooleanValue macro = new BooleanValue("Macro", false);
	public BooleanValue motion = new BooleanValue("Motion", false);
	public DoubleValue range = new DoubleValue("Range", 5, 1, 8, 0.1);
	public IntValue bpt = new IntValue("BPT", 3, 1, 8, 1);
	public BooleanValue friends = new BooleanValue("Friends", false);
	private BlockposYComparator comparator = new BlockposYComparator();
	private BlockPos centerPos;
	private int tickDelay;
	private int slot = 0;
	
	public AutoTrapModule() {
		super("AutoTrap", "Surround your enemy in obsidian", Category.COMBAT);
	}
	
	@Override
	public void onEnable() {
		if (mc.player != null) slot = mc.player.inventory.currentItem;
	}
	
	@Override
	public void onDisable() {
		if (mc.player != null) mc.player.inventory.currentItem = slot;
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		List<BlockPos> toPlace = new ArrayList<>();
		EntityPlayer target = getTarget(getTargets());
		if (!MinecraftUtils.isHoldingBlock(Blocks.OBSIDIAN)) slot = mc.player.inventory.currentItem;
		if (target == null) return;
		if (motion.getValue() && !(target.motionX == 0 && target.motionY == 0 && target.motionZ == 0)) return;
		double x = (int) target.posX < 0 ? ((int) target.posX - 0.5) : ((int) target.posX + 0.5);
		double z = (int) target.posZ < 0 ? ((int) target.posZ - 0.5) : ((int) target.posZ + 0.5);
		centerPos = new BlockPos(x, target.posY, z);
		if (MinecraftUtils.isTrapped(centerPos)) {
			if (macro.getValue()) toggle();
			mc.player.inventory.currentItem = slot;
			return;
		}
		if (!MinecraftUtils.holdBlock(Blocks.OBSIDIAN)) return;
		tickDelay = 0;
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
			if (!MinecraftUtils.canPlace(centerPos.offset(facing), Blocks.OBSIDIAN, false))
				toPlace.add(centerPos.offset(facing).down());
			toPlace.add(centerPos.offset(facing));
			toPlace.add(centerPos.offset(facing).up());
		}
		if (!MinecraftUtils.canPlace(centerPos.up(2), Blocks.OBSIDIAN, false))
			toPlace.add(centerPos.up(2).offset(mc.player.getHorizontalFacing()));
		toPlace.add(centerPos.up(2));
		if (mode.getValue().equalsIgnoreCase("antistep")) {
			if (!MinecraftUtils.canPlace(centerPos.up(3), Blocks.OBSIDIAN, false))
				toPlace.add(centerPos.up(3).offset(mc.player.getHorizontalFacing()));
			toPlace.add(centerPos.up(3));
		}
		toPlace.sort(comparator);
		for (BlockPos pos : toPlace) {
			if (tickDelay >= bpt.getValue()) return;
			if (MinecraftUtils.canPlace(pos, Blocks.OBSIDIAN, true)) {
				MinecraftUtils.place(pos);
				tickDelay++;
			}
		}
	}
	
	private List<EntityPlayer> getTargets() {
		ArrayList<EntityPlayer> targetlist = new ArrayList<>();
		Iterator targets = mc.world.loadedEntityList.iterator();
		while (targets.hasNext()) {
			Entity en = (Entity) targets.next();
			if (en == null) continue;
			if (!(en instanceof EntityPlayer)) continue;
			EntityPlayer e = (EntityPlayer) en;
			if (mc.player.getDistance(e) > 8) continue;
			if (e == mc.player) continue;
			if (e.getHealth() <= 0 || e.isDead) continue;
			if (!friends.getValue() && Raion.INSTANCE.getFriendManager().isFriend(e)) continue;
			targetlist.add(e);
		}
		return targetlist;
	}
	
	private EntityPlayer getTarget(List<EntityPlayer> targets) {
		EntityPlayer target = null;
		double distance = range.getValue();
		for (EntityPlayer entity : targets) {
			if (mc.player.getDistance(entity) < distance) {
				distance = mc.player.getDistance(entity);
				target = entity;
			}
		}
		return target;
	}
	
	static class BlockposYComparator implements java.util.Comparator<BlockPos> {
		public int compare(BlockPos b1, BlockPos b2) {
			final int y1 = b1.getY();
			final int y2 = b2.getY();
			return Integer.compare(y1, y2);
		}
	}
}
