package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.events.events.world.LiquidCollisionBBEvent;
import me.robeart.raion.client.imixin.ICPacketPlayer;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.FloatValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Arrays;

public class JesusModule extends Module {
	
	private static final AxisAlignedBB WATER_WALK_AA = new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D);
	public ListValue mode = new ListValue("Mode", "Default", Arrays.asList("Default", "Bounce", "Trampoline"));
	public FloatValue offset = new FloatValue("Offset", 0.05f, 0.01f, 0.5f, 0.01f);
	
	public JesusModule() {
		super("Jesus", "Lets you walk on liquids", Category.MOVEMENT);
	}
	
	public static boolean isInLiquid() {
		if (mc.player.fallDistance >= 3.0F) return false;
		if (mc.player != null) {
			boolean inLiquid = false;
			AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity()
				.getEntityBoundingBox() : mc.player.getEntityBoundingBox();
			int y = (int) bb.minY;
			for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
				for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
					Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (!(block instanceof BlockAir)) {
						if (!(block instanceof BlockLiquid)) return false;
						inLiquid = true;
					}
				}
			}
			return inLiquid;
		}
		return false;
	}
	
	public static boolean isOnLiquid(double offset) {
		if (mc.player.fallDistance >= 3.0F) return false;
		if (mc.player != null) {
			AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity()
				.getEntityBoundingBox()
				.contract(0.0D, 0.0D, 0.0D)
				.offset(0.0D, -offset, 0.0D) : mc.player.getEntityBoundingBox()
				.contract(0.0D, 0.0D, 0.0D)
				.offset(0.0D, -offset, 0.0D);
			boolean onLiquid = false;
			int y = (int) bb.minY;
			for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
				for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
					Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (block != net.minecraft.init.Blocks.AIR) {
						if (!(block instanceof BlockLiquid)) return false;
						onLiquid = true;
					}
				}
			}
			return onLiquid;
		}
		return false;
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		if (mode.getValue().equalsIgnoreCase("Trampoline")) {
			if ((!mc.player.isSneaking()) && (!mc.player.noClip) && (!mc.gameSettings.keyBindJump.isKeyDown())) {
				if (mc.player.fallDistance <= 3f) {
					if (mc.player.isInLava() || mc.player.isInWater()) {
						mc.player.motionY = 1.273197475E-314;
					}
					else {
						mc.player.motionY = 1.273197475E-314;
					}
				}
			}
		}
		else {
			if ((!mc.player.isSneaking()) && (!mc.player.noClip) && (!mc.gameSettings.keyBindJump.isKeyDown()) && (isInLiquid()))
				mc.player.motionY = 0.1;
		}
	}
	
	@Listener
	private void onLiquidCollisionBB(LiquidCollisionBBEvent event) {
		if ((mc.world != null) && (mc.player != null) &&
			(checkCollide()) && (mc.player.motionY < 0.1) && (event.getBlockPos()
			.getY() < mc.player.posY - offset.getValue())) {
			if (mc.player.getRidingEntity() != null) {
				event.setBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0F - offset.getValue(), 1.0D));
			}
			else if (mode.getValue().equalsIgnoreCase("Bounce")) {
				event.setBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9, 1.0D));
			}
			else {
				event.setBoundingBox(Block.FULL_BLOCK_AABB);
			}
			event.setCanceled(true);
		}
	}
	
	@Listener
	private void onPacketSend(PacketSendEvent event) {
		if (((event.getPacket() instanceof CPacketPlayer)) && event.getStage() == EventStageable.EventStage.PRE && (mode
			.getValue()
			.equalsIgnoreCase("Bounce")) && (mc.player.getRidingEntity() == null) && (!mc.gameSettings.keyBindJump.isKeyDown())) {
			CPacketPlayer packet = (CPacketPlayer) event.getPacket();
			if ((!isInLiquid()) && (isOnLiquid(offset.getValue())) && (checkCollide()) && (Minecraft.getMinecraft().player.ticksExisted % 3 == 0)) {
				((ICPacketPlayer) packet).setY(packet.getY(0) - offset.getValue());
			}
		}
	}
	
	private boolean checkCollide() {
		if (mc.player.isSneaking()) return false;
		if ((mc.player.getRidingEntity() != null) && (mc.player.getRidingEntity().fallDistance >= 3.0F)) return false;
		return !(mc.player.fallDistance >= 3.0F);
	}
	
}
