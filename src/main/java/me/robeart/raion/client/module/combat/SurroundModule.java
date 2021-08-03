package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.util.minecraft.RenderUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.ColorValue;
import me.robeart.raion.client.value.DoubleValue;
import me.robeart.raion.client.value.IntValue;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SurroundModule extends Module {
	
	public BooleanValue rotate = new BooleanValue("Rotate", false);
	public BooleanValue swing = new BooleanValue("Swing", false);
	public BooleanValue macro = new BooleanValue("Macro", false);
	public IntValue bpt = new IntValue("BPT", 3, 1, 8, 1);
	public BooleanValue tp = new BooleanValue("Teleport", false);
	public DoubleValue delay = new DoubleValue("Delay", 0, 0, 20, 0.1);
	public BooleanValue renderb = new BooleanValue("Render", true);
	public ColorValue colorV = new ColorValue("Color", Color.RED, false);
	private BlockposYComparator comparator = new BlockposYComparator();
	private Timer delaytimer = new Timer();
	private BlockPos centerpos;
	private float alpha;
	private int tickdelay;
	private boolean hasPlaced = false;
	
	public SurroundModule() {
		super("Surround", "Surrounds yourself with obsidian", Category.COMBAT);
	}
	
	@Override
	public void onEnable() {
	}
	
	@Override
	public void onDisable() {
	}
	
	@Listener
	public void onRender(Render3DEvent event) {
		if (renderb.getValue() && centerpos != null) {
			alpha = Interpolation.finterpTo(alpha, hasPlaced ? 0.32f : 0.0f, event.getPartialTicks(), 0.1f);
			
			if (alpha != 0) {
				Color color = colorV.getValue();
				RenderUtils.blockEsp(Collections.singleton(centerpos), color.getRed() / 255f, color.getBlue() / 255f, color.getGreen() / 255f, alpha, 1, 1, 1);
			}
		}
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		hasPlaced = false;
		int previousSlot = mc.player.inventory.currentItem;
		try {
			if (mc.player.onGround && Math.abs(mc.player.motionX) < 0.01 && Math.abs(mc.player.motionZ) < 0.01) {
				tickdelay = 0;
				if (!delaytimer.passed(delay.getValue() * 100)) return;
				double x = (int) mc.player.posX < 0 ? ((int) mc.player.posX - 0.5) : ((int) mc.player.posX + 0.5);
				double z = (int) mc.player.posZ < 0 ? ((int) mc.player.posZ - 0.5) : ((int) mc.player.posZ + 0.5);
				centerpos = new BlockPos(x, mc.player.posY, z);
				if (MinecraftUtils.isHole(centerpos)) {
					if (macro.getValue()) toggle();
					return;
				}
				boolean teleported = false;
				List<BlockPos> toPlace = new ArrayList<>();
				BlockPos.PooledMutableBlockPos blockPos = BlockPos.PooledMutableBlockPos.retain();
				for (EnumFacing facing : EnumFacing.values()) {
					if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) continue;
					blockPos.setPos(centerpos.getX() + facing.getXOffset(), centerpos.getY() + facing.getYOffset(), centerpos.getZ() + facing.getZOffset());
					if (!MinecraftUtils.canPlace(blockPos, Blocks.OBSIDIAN, false)) {
						toPlace.add(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ()));
					}
					toPlace.add(new BlockPos(blockPos));
				}
				blockPos.release();
				toPlace.sort(comparator);
				for (BlockPos pos : toPlace) {
					if (tickdelay >= bpt.getValue()) return;
					if (MinecraftUtils.canPlace(pos, Blocks.OBSIDIAN, true)) {
						if (!MinecraftUtils.holdBlock(Blocks.OBSIDIAN)) return;
						if (!teleported) {
							teleported = true;
							if (tp.getValue()) {
								teleportTo(x, mc.player.posY, z);
							}
						}
						MinecraftUtils.place(pos, true, swing.getValue(), rotate.getValue());
						hasPlaced = true;
						tickdelay++;
					}
				}
			}
			else if (centerpos != null && (!(mc.player.posX < (double) centerpos.getX() + 0.9 && mc.player.posX > (double) centerpos.getX() - 0.9) || !(mc.player.posZ < (double) centerpos.getZ() + 0.9 && mc.player.posZ > (double) centerpos.getZ() - 0.9))) {
				delaytimer.reset();
			}
		}
		finally {
			mc.player.inventory.currentItem = previousSlot;
		}
	}
	
	private void teleportTo(double x, double y, double z) {
		if (mc.player.getDistance(x, y, z) > 0.4) mc.player.setPosition(x - 0.2, y, z - 0.2);
		else mc.player.setPosition(x, y, z);
	}
	
	class BlockposYComparator implements java.util.Comparator<BlockPos> {
		public int compare(BlockPos b1, BlockPos b2) {
			final int y1 = b1.getY();
			final int y2 = b2.getY();
			return Integer.compare(y1, y2);
		}
	}
	
}
