package me.robeart.raion.client.util.minecraft;

import me.robeart.raion.client.managers.RotationManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.Iterator;

public class MinecraftUtils {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	private static Field fastRenderField;
	
	static {
		try {
			fastRenderField = GameSettings.class.getDeclaredField("ofFastRender");
			
			if (!fastRenderField.isAccessible())
				fastRenderField.setAccessible(true);
		}
		catch (final NoSuchFieldException ignored) {
		}
	}
	
	public static void quickLogout() {
		if (mc.player == null) return;
		if (mc.getConnection() == null) return;
		
		mc.getConnection().sendPacket(new CPacketUseEntity(mc.player));
	}
	
	public static int getSlotOfItem(Item input) {
		for (int i = 0; i < 36; i++) {
			Item item = mc.player.inventory.getStackInSlot(i).getItem();
			if (item == input) return i < 9 ? i + 36 : i;
		}
		return -1;
	}
	
	
	public static int getSlotOfItemReverse(Item input) {
		for (int i = 35; i < -1; i--) {
			Item item = mc.player.inventory.getStackInSlot(i).getItem();
			if (item == input) return i < 9 ? i + 36 : i;
		}
		return -1;
	}
	
	/**
	 * Find the entities interpolated amount
	 */
	public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
		return new Vec3d(
			(entity.posX - entity.lastTickPosX) * x,
			(entity.posY - entity.lastTickPosY) * y,
			(entity.posZ - entity.lastTickPosZ) * z
		);
	}
	
	public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
		return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
	}
	
	public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
		return getInterpolatedAmount(entity, ticks, ticks, ticks);
	}
	
	/**
	 * Find the entities interpolated position
	 */
	public static Vec3d getInterpolatedPos(Entity entityIn, double partialTicks) {
		double x = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
		double y = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
		double z = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
		return new Vec3d(x, y, z);
	}
	
	/**
	 * Find the entities interpolated eye position
	 */
	public static Vec3d getInterpolatedEyePos(Entity entity, double ticks) {
		return getInterpolatedPos(entity, ticks).add(0, entity.getEyeHeight(), 0);
	}
	
	public static boolean isPassive(Entity e) {
		if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) return false;
		if (e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntityAmbientCreature || e instanceof EntitySquid)
			return true;
		return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
	}
	
	public static boolean isMobAggressive(Entity entity) {
		if (entity instanceof EntityPigZombie) {
			// arms raised = aggressive, angry = either game or we have set the anger cooldown
			if (((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()) {
				return true;
			}
		}
		else if (entity instanceof EntityWolf) {
			return ((EntityWolf) entity).isAngry() &&
				!Minecraft.getMinecraft().player.equals(((EntityWolf) entity).getOwner());
		}
		else if (entity instanceof EntityEnderman) {
			return ((EntityEnderman) entity).isScreaming();
		}
		return isHostileMob(entity);
	}
	
	public static boolean isHostileMob(Entity entity) {
		return (isCreatureType(entity, EnumCreatureType.MONSTER, false) && !isNeutralMob(entity));
	}
	
	public static boolean isNeutralMob(Entity entity) {
		return entity instanceof EntityPigZombie ||
			entity instanceof EntityWolf ||
			entity instanceof EntityEnderman;
	}
	
	public static boolean isFriendlyMob(Entity entity) {
		return (isCreatureType(entity, EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)) ||
			(isCreatureType(entity, EnumCreatureType.AMBIENT, false)) ||
			entity instanceof EntityVillager ||
			entity instanceof EntityIronGolem ||
			(isNeutralMob(entity) && !isMobAggressive(entity));
	}
	
	public static int getHotbarSlotOfItem(Item input) {
		for (int i = 0; i < 9; ++i) {
			Item item = mc.player.inventory.getStackInSlot(i).getItem();
			if (item == input) return i;
		}
		return -1;
	}
	
	public static int getHotbarSlotOfBlock(Block type) {
		for (int i = 0; i < 9; ++i) {
			ItemStack stack = mc.player.inventory.getStackInSlot(i);
			if (stack.getItem() instanceof ItemBlock) {
				ItemBlock block = (ItemBlock) stack.getItem();
				if (block.getBlock() == type) return i;
			}
		}
		return -1;
	}
	
	public static int getItemCount(Item input) {
		int items = 0;
		for (int i = 0; i < 45; i++) {
			ItemStack stack = mc.player.inventory.getStackInSlot(i);
			if (stack.getItem() == input) items += stack.getCount();
		}
		return items;
	}
	
	public static boolean isHole(BlockPos blockPos) {
		return !Blocks.OBSIDIAN.canPlaceBlockAt(mc.world, blockPos.north())
			&& !Blocks.OBSIDIAN.canPlaceBlockAt(mc.world, blockPos.east())
			&& !Blocks.OBSIDIAN.canPlaceBlockAt(mc.world, blockPos.south())
			&& !Blocks.OBSIDIAN.canPlaceBlockAt(mc.world, blockPos.west());
	}
	
	public static boolean isTrapped(BlockPos blockPos) {
		return (isHole(blockPos)
			&& isHole(blockPos.up())
			&& !Blocks.OBSIDIAN.canPlaceBlockAt(mc.world, blockPos.up().up()));
	}
	
	public static void holdItemFromInventory(Item item) {
		if (!isHoldingItem(item)) {
			int i = getSlotOfItem(item);
			if (i != -1) {
				if (i > 35) mc.player.inventory.currentItem = i - 36;
				else {
					int air = getHotbarSlotOfItem(Items.AIR);
					if (air != -1) {
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, air + 36, 0, ClickType.PICKUP, mc.player);
						//mc.playerController.updateController();
					}
					else {
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, mc.player.inventory.currentItem + 36, 0, ClickType.PICKUP, mc.player);
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
						//mc.playerController.updateController();
					}
				}
			}
		}
	}
	
	public static boolean holdBlock(Block block) {
		if (!isHoldingBlock(block)) {
			int i = getHotbarSlotOfBlock(block);
			if (i != -1) {
				mc.player.inventory.currentItem = i;
				return true;
			}
			return false;
		}
		return true;
	}
	
	public static boolean holdItem(Item item) {
		if (!isHoldingItem(item)) {
			int i = getHotbarSlotOfItem(item);
			if (i != -1) {
				mc.player.inventory.currentItem = i;
				return true;
			}
			return false;
		}
		return true;
	}
	
	public static boolean canEntityFeetBeSeen(Entity entityIn) {
		return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
	}
	
	public static boolean isHoldingBlock(Block input) {
		if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemBlock) {
			ItemBlock block = (ItemBlock) mc.player.inventory.getCurrentItem().getItem();
			return block.getBlock() == input;
		}
		return false;
	}
	
	public static boolean isHoldingItem(Item item) {
		return mc.player.inventory.getCurrentItem().getItem() == item;
	}
	
	public static void disableFastRender() {
		try {
			if (fastRenderField != null) {
				if (!fastRenderField.isAccessible())
					fastRenderField.setAccessible(true);
				
				fastRenderField.setBoolean(mc.gameSettings, false);
			}
		}
		catch (final IllegalAccessException ignored) {
		}
	}
	
	public static void lookAt(double px, double py, double pz) {
		double dirx = mc.player.posX - px;
		double diry = mc.player.posY - py;
		double dirz = mc.player.posZ - pz;
		
		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
		
		dirx /= len;
		diry /= len;
		dirz /= len;
		
		double pitch = Math.asin(diry);
		double yaw = Math.atan2(dirz, dirx);
		
		//to degree
		pitch = pitch * 180.0d / Math.PI;
		yaw = yaw * 180.0d / Math.PI;
		
		yaw += 90f;
		
		RotationManager.INSTANCE.sendRotation(
			(float) yaw, (float) pitch, mc.player.onGround
		);
	}
	
	public static Iterable<BlockPos> getBlocksInChunkIterable(int chunkX, int chunkZ) {
		return () -> getBlocksInChunk(chunkX, chunkZ);
	}
	
	public static Iterator<BlockPos> getBlocksInChunk(int chunkX, int chunkZ) {
		return new Iterator<BlockPos>() {
			int x;
			int y;
			int z;
			
			@Override
			public boolean hasNext() {
				return x < 16 && y < 16 && z < 16;
			}
			
			@Override
			public BlockPos next() {
				BlockPos out = new BlockPos(x + chunkX, y, z + chunkZ);
				
				if (y < x) {
					y += 1;
				}
				else if (z < y) {
					z += 1;
				}
				else {
					x += 1;
				}
				
				return out;
			}
		};
	}
	
	public static Iterable<BlockPos> getBlocksInRadius(double radius, double yOffset) {
		if (mc.player == null) {
			throw new NullPointerException("Player was null");
		}
		return getBlocksInRadius(radius, yOffset, mc.player);
	}
	
	public static Iterable<BlockPos> getBlocksInRadius(double radius, double yOffset, Entity target) {
		if (target == null) {
			throw new NullPointerException("Invalid target of null");
		}
		BlockPos playerPos = mc.player.getPosition(); // Or some other method of getting a BlockPos of the player
		BlockPos positiveRadiusPosition = playerPos.add(radius, yOffset, radius); // Gets one corner of the cube in the positive X, Y, and Z direction
		BlockPos negativeRadiusPosition = playerPos.add(-1 * radius, -1 * yOffset, -1 * radius); // Gets the opposite corner
		return BlockPos.getAllInBox(positiveRadiusPosition, negativeRadiusPosition);
	}
	
	public static Iterable<BlockPos.MutableBlockPos> getBlocksInRadiusMutable(double radius, double yOffset) {
		if (mc.player == null) {
			throw new NullPointerException("Player was null");
		}
		return getBlocksInRadiusMutable(radius, yOffset, mc.player);
	}
	
	public static Iterable<BlockPos.MutableBlockPos> getBlocksInRadiusMutable(double radius, double yOffset, Entity target) {
		if (target == null) {
			throw new NullPointerException("Invalid target of null");
		}
		int minX = MathHelper.floor(mc.player.posX - radius);
		int minY = MathHelper.floor(mc.player.posY - yOffset);
		int minZ = MathHelper.floor(mc.player.posZ - radius);
		int maxX = MathHelper.ceil(mc.player.posX + radius);
		int maxY = MathHelper.ceil(mc.player.posY + yOffset);
		int maxZ = MathHelper.ceil(mc.player.posZ + radius);
		return BlockPos.getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public static boolean canPlace(BlockPos pos, Block block, boolean checkEntity) {
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos blockPos = pos.offset(facing);
			Block posBlock = mc.world.getBlockState(blockPos).getBlock();
			if (posBlock != Blocks.AIR && !(posBlock instanceof BlockLiquid) && block.canPlaceBlockAt(mc.world, pos)) {
				if (checkEntity) {
					if (mc.world.checkNoEntityCollision(new AxisAlignedBB(pos))) {
						return true;
					}
				}
				else return true;
			}
		}
		return false;
	}
	
	public static boolean place(BlockPos pos) {
		return place(pos, true, true, true);
	}
	
	public static boolean place(BlockPos pos, boolean sneak, boolean swing, boolean rotate) {
		boolean sneaking = mc.player.isSneaking();
		try {
			Vec3d eyesPos = new Vec3d(
				mc.player.posX,
				mc.player.posY + mc.player.getEyeHeight(),
				mc.player.posZ
			);
			
			for (EnumFacing side : EnumFacing.values()) {
				BlockPos neighbor = pos.offset(side);
				EnumFacing side2 = side.getOpposite();
				
				// check if side is visible (facing away from player)
            /*if(eyesPos.squareDistanceTo(
                new Vec3d(pos).addVector(0.5, 0.5, 0.5)) >= eyesPos
                .squareDistanceTo(
                    new Vec3d(neighbor).addVector(0.5, 0.5, 0.5)))
                continue;*/
				
				// check if neighbor can be right clicked
				if (mc.world.getBlockState(neighbor).getBlock() == Blocks.AIR || (mc.world.getBlockState(neighbor)
					.getBlock() instanceof BlockLiquid))
					continue;
				
				Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
					.add(new Vec3d(side2.getDirectionVec()).scale(0.5));
				
				// check if hitVec is within range (4.25 blocks)
				if (eyesPos.squareDistanceTo(hitVec) > 18.0625)
					continue;
				
				// place block
				if (rotate) {
					double diffX = hitVec.x - eyesPos.x;
					double diffY = hitVec.y - eyesPos.y;
					double diffZ = hitVec.z - eyesPos.z;
					
					double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
					
					float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
					float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
					
					float[] rotations = {
						mc.player.rotationYaw
							+ MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
						mc.player.rotationPitch +
							MathHelper
								.wrapDegrees(pitch - mc.player.rotationPitch)
					};
					
					RotationManager.INSTANCE.sendRotation(rotations[0], rotations[1], mc.player.onGround);
				}
				if (sneak) {
					mc.player.setSneaking(true);
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
				}
				boolean success = false;
				mc.playerController.updateController();
				if (mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
					if (swing) {
						mc.player.swingArm(EnumHand.MAIN_HAND);
					}
					success = true;
				}
				if (sneak) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
				}
				
				if (success) {
					return true;
				}
			}
			return false;
		}
		finally {
			mc.player.setSneaking(sneaking);
		}
	}
	
	public static void place(BlockPos pos, EnumFacing facing) {
		Vec3d eyesPos = new Vec3d(
			mc.player.posX,
			mc.player.posY + mc.player.getEyeHeight(),
			mc.player.posZ
		);
		
		for (EnumFacing side : EnumFacing.values()) {
			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();
			
			// check if side is visible (facing away from player)
            /*if(eyesPos.squareDistanceTo(
                new Vec3d(pos).addVector(0.5, 0.5, 0.5)) >= eyesPos
                .squareDistanceTo(
                    new Vec3d(neighbor).addVector(0.5, 0.5, 0.5)))
                continue;*/
			
			// check if neighbor can be right clicked
			if (mc.world.getBlockState(neighbor).getBlock() == Blocks.AIR || (mc.world.getBlockState(neighbor)
				.getBlock() instanceof BlockLiquid))
				continue;
			
			Vec3d hitVec = new Vec3d(neighbor).add(0.9, 0.9, 0.9)
				.add(new Vec3d(side2.getDirectionVec()).scale(0.5));
			
			// check if hitVec is within range (4.25 blocks)
			if (eyesPos.squareDistanceTo(hitVec) > 18.0625)
				continue;
			
			// place block
			double diffX = hitVec.x - eyesPos.x;
			double diffY = hitVec.y - eyesPos.y;
			double diffZ = hitVec.z - eyesPos.z;
			
			double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
			
			float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
			float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
			
			float[] rotations = {
				mc.player.rotationYaw
					+ MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
				mc.player.rotationPitch +
					MathHelper
						.wrapDegrees(pitch - mc.player.rotationPitch)
			};
			
			//final boolean activated = mc.world.getBlockState(neighbor).getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
			
			RotationManager.INSTANCE.sendRotation(rotations[0], rotations[1], mc.player.onGround);
			mc.player.setSneaking(true);
			//mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			if (mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, facing, hitVec, EnumHand.MAIN_HAND) != EnumActionResult.FAIL)
				mc.player.swingArm(EnumHand.MAIN_HAND);
			mc.player.setSneaking(false);
			//mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			
			return;
		}
	}
	
	private static boolean isCreatureType(Entity e, EnumCreatureType type, boolean forSpawnCount) {
		if (forSpawnCount && (e instanceof EntityLiving) && ((EntityLiving) e).isNoDespawnRequired()) return false;
		return type.getCreatureClass().isAssignableFrom(e.getClass());
	}
	
	static class DistanceComparator implements java.util.Comparator<BlockPos> {
		private Entity target;
		
		public DistanceComparator(Entity e) {
			target = e;
		}
		
		public int compare(BlockPos b1, BlockPos b2) {
			final double b1Distance = target.getDistanceSq(b1);
			final double b2Distance = target.getDistanceSq(b2);
			return Double.compare(b1Distance, b2Distance);
		}
	}
	
}
