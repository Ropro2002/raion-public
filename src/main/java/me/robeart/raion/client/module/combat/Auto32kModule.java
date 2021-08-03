package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.DoubleValue;
import me.robeart.raion.client.value.IntValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

/**
 * @author Robeart
 */
public class Auto32kModule extends Module {

	private static boolean isSpoofingAngles;
	private static double yaw;
	private static double pitch;
	public DoubleValue rangeplayer = new DoubleValue("Player Range", 6, 1, 10, 0.1);
	public DoubleValue rangeAttack = new DoubleValue("Attack Range", 3.6, 1, 10, 0.1);
	public BooleanValue armor = new BooleanValue("Check Armor", false);
	public BooleanValue block = new BooleanValue("Block Shulker", false);
	public BooleanValue aura = new BooleanValue("KillAura", true);
	public ListValue mode = new ListValue("Attack Mode", "OnUpdate", Arrays.asList("OnUpdate", "OnRender"));
	public DoubleValue delay = new DoubleValue("Delay", 0.02, 0, 1, 0.01, mode, "OnRender");
	public IntValue tickdelay = new IntValue("Ticks", 3, 0, 20, 1, mode, "OnUpdate");
	public BooleanValue friends = new BooleanValue("Friends", false);
	public BooleanValue walls = new BooleanValue("Walls", true);
	private boolean placed;
	private int ticks;
	private EntityPlayer target;
	private BlockPos hopper;
	private BlockPos dispenser;
	private boolean redstonePlaced;
	private int hopperSlot;
	private int shulkerSlot;
	private int blockSlot;

	private Timer timer = new Timer();
	private Timer hopperTimer = new Timer();

	public Auto32kModule() {
		super("Auto32k", "Automatically kills someone using 32k weapons", Category.COMBAT);
	}

	public static EnumFacing getFacing(BlockPos pos) {
		Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
		Vec3d hitVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(EnumFacing.DOWN.getDirectionVec()).scale(0.5));
		double diffX = hitVec.x - eyesPos.x;
		double diffZ = hitVec.z - eyesPos.z;
		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float yaw2 = mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
		if (Math.abs(mc.player.posX - (double) ((float) pos.getX() + 0.5F)) < 2.0D && Math.abs(mc.player.posZ - (double) ((float) pos
			.getZ() + 0.5F)) < 2.0D) {
			double d0 = mc.player.posY + (double) mc.player.getEyeHeight();
			if (d0 - (double) pos.getY() > 2.0D) {
				return UP;
			}
			if ((double) pos.getY() - d0 > 0.0D) {
				return DOWN;
			}
		}
		return EnumFacing.byHorizontalIndex(MathHelper.floor((double) (yaw2 * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
	}

	@Override
	public void onEnable() {
		redstonePlaced = false;
		ticks = 0;
		placed = false;
	}

	@Listener
	private void onUpdate(OnUpdateEvent event) {
		target = getTarget(getTargets());
		if (!placed) {
			hopperSlot = MinecraftUtils.getHotbarSlotOfBlock(Blocks.HOPPER);
			shulkerSlot = getShulkerSlot();
			int dispenserSlot = MinecraftUtils.getHotbarSlotOfBlock(Blocks.DISPENSER);
			int redstoneSlot = MinecraftUtils.getHotbarSlotOfBlock(Blocks.REDSTONE_BLOCK);
			blockSlot = MinecraftUtils.getHotbarSlotOfBlock(Blocks.OBSIDIAN);
			if (hopperSlot == -1 || shulkerSlot == -1 || dispenserSlot == -1 || redstoneSlot == -1 || blockSlot == -1)
				return;
			if (target == null) placeThings(mc.player, dispenserSlot, redstoneSlot, blockSlot);
			else placeThings(target, dispenserSlot, redstoneSlot, blockSlot);
			placed = true;
		}
		if ((mc.currentScreen instanceof GuiDispenser)) {
			EnumFacing facingdispenser = getFacing(dispenser);
			if (!(mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(0)
				.getItem() instanceof ItemShulkerBox)) {
				if (mc.world.getBlockState(hopper.up()).getBlock() instanceof BlockShulkerBox) {
					BlockPos obby = dispenser.offset(facingdispenser).offset(facingdispenser);
					if (block.getValue() && Blocks.OBSIDIAN.canPlaceBlockAt(mc.world, obby)
						&& mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(obby)).isEmpty())
						MinecraftUtils.place(obby);
					mc.player.inventory.currentItem = hopperSlot;
					MinecraftUtils.place(hopper);
					mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(hopper, UP, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
				}
				else {
					mc.playerController.windowClick(mc.player.openContainer.windowId, 0, shulkerSlot, ClickType.SWAP, mc.player);
				}
			}
			else {
				if (!redstonePlaced) {
					EnumFacing facing = canPlaceDispenser(dispenser, facingdispenser);
					MinecraftUtils.place(dispenser.offset(facing));
					mc.player.inventory.currentItem = blockSlot;
					redstonePlaced = true;
				}
			}
		}

		if (!is32k(mc.player.getHeldItemMainhand())) {
			if ((mc.currentScreen instanceof GuiHopper)) {
				int swapslot = MinecraftUtils.getHotbarSlotOfItem(Items.AIR) == -1 ? mc.player.inventory.currentItem : MinecraftUtils
					.getHotbarSlotOfItem(Items.AIR);
				for (int i = 0; i < 5; i++) {
					if (is32k(mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i))) {
						mc.playerController.windowClick(mc.player.openContainer.windowId, i, swapslot, ClickType.SWAP, mc.player);
						mc.player.inventory.currentItem = swapslot;
						break;
					}
				}
			}
		}
		if (aura.getValue() && is32k(mc.player.getHeldItemMainhand()) && target != null && mode.getValue()
			.equalsIgnoreCase("onUpdate")) {
			if (ticks >= tickdelay.getValue() && mc.player.getDistance(target) <= rangeAttack.getValue()) {
				mc.playerController.attackEntity(mc.player, target);
				mc.player.swingArm(EnumHand.MAIN_HAND);
				ticks = 0;
			}
		}
		ticks++;
	}

	private boolean is32k(ItemStack itemStack) {
		return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= 1000;
	}

    /*private EnumFacing getFacing(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ);
        Vec3d hitVec = new Vec3d(pos).add(0.5, 0.5, 0.5)
                .add(new Vec3d(EnumFacing.DOWN.getDirectionVec()).scale(0.5));
        double diffX = hitVec.x - eyesPos.x;
        double diffZ = hitVec.z - eyesPos.z;
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float yaw2 = mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
        EnumFacing facing = EnumFacing.byHorizontalIndex(MathHelper.floor((double)(yaw2 * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        return facing;
    }*/

	private void placeThings(EntityLivingBase target, int dispenserIndex, int redstoneIndex, int blockIndex) {
		BlockPos block = mc.player == target ? bestPlace() : bestPlace(target);
		if (block == null) return;
		this.dispenser = block.up();
		EnumFacing facing = getFacing(dispenser);
		this.hopper = block.offset(facing);
		mc.player.inventory.currentItem = blockIndex;
		MinecraftUtils.place(block);
		mc.player.inventory.currentItem = dispenserIndex;
		MinecraftUtils.place(dispenser);
		mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(dispenser, UP, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
		mc.player.inventory.currentItem = redstoneIndex;
	}

	private int getShulkerSlot() {
		for (int i = 0; i < 9; ++i) {
			ItemStack stack = mc.player.inventory.getStackInSlot(i);
			if (stack.getItem() instanceof ItemShulkerBox) {
				return i;
			}
		}
		return -1;
	}

	private List<EntityPlayer> getTargets() {
		ArrayList<EntityPlayer> targetlist = new ArrayList<>();
		Iterator targets = mc.world.loadedEntityList.iterator();
		while (targets.hasNext()) {
			Entity en = (Entity) targets.next();
			if (en == null) continue;
			if (!(en instanceof EntityPlayer)) continue;
			EntityPlayer e = (EntityPlayer) en;
			if (mc.player.getDistance(e) > rangeplayer.getValue()) continue;
			if (e == mc.player) continue;
			if (!walls.getValue() && !mc.player.canEntityBeSeen(e) && !MinecraftUtils.canEntityFeetBeSeen(e)) continue;
			if (e.getHealth() <= 0 || e.isDead) continue;
			if (!friends.getValue() && Raion.INSTANCE.getFriendManager().isFriend(e)) continue;
			targetlist.add(e);
		}
		return targetlist;
	}

	private EntityPlayer getTarget(List<EntityPlayer> targets) {
		EntityPlayer target = null;
		double distance = rangeplayer.getValue();
		for (EntityPlayer entity : targets) {
			if (armor.getValue() && !(target.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
				.getItem() == Items.DIAMOND_HELMET
				|| target.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.DIAMOND_LEGGINGS
				|| target.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.DIAMOND_BOOTS))
				continue;
			if (mc.player.getDistance(entity) < distance) {
				distance = mc.player.getDistance(entity);
				target = entity;
			}
		}
		return target;
	}

	private BlockPos bestPlace(EntityLivingBase target) {
		BlockPos blockPos = null;
		double distance = 0;
		for (BlockPos pos : canPlaceBlocks()) {
			if (target.getDistance(pos.getX(), pos.getY(), pos.getZ()) > distance) {
				blockPos = pos;
			}
		}
		return blockPos;
	}

	private BlockPos bestPlace() {
		BlockPos blockPos = null;
		double distance = 420;
		for (BlockPos pos : canPlaceBlocks()) {
			if (mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < distance) {
				blockPos = pos;
			}
		}
		return blockPos;
	}

	private List<BlockPos> canPlaceBlocks() {
		if (mc.player == null) return null;
		List<BlockPos> blockPosList = new ArrayList<>();
		for (BlockPos pos : MinecraftUtils.getBlocksInRadius(3, 3)) {
			EnumFacing facing = getFacing(pos.up());
			if (facing == null || facing == DOWN || facing == UP) continue;
			if (MinecraftUtils.canPlace(pos, Blocks.OBSIDIAN, true)
				&& Blocks.DISPENSER.canPlaceBlockAt(mc.world, pos.up())
				&& mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up())).isEmpty()
				&& (canPlaceDispenser(pos.up(), facing) != null)
				&& Blocks.HOPPER.canPlaceBlockAt(mc.world, pos.offset(facing))
				&& mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.offset(facing))).isEmpty()
				&& Blocks.SILVER_SHULKER_BOX.canPlaceBlockAt(mc.world, pos.offset(facing).up())
				&& mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.offset(facing).up()))
				.isEmpty()) {
				blockPosList.add(pos);
			}
		}
		return blockPosList;
	}

	private EnumFacing canPlaceDispenser(BlockPos pos, EnumFacing enumFacing) {
		for (EnumFacing f : EnumFacing.values()) {
			if (f == DOWN || f == enumFacing) continue;
			if (Blocks.REDSTONE_BLOCK.canPlaceBlockAt(mc.world, pos.offset(f))
				&& mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.offset(f))).isEmpty())
				return f;
		}
		return null;
	}

	@Listener
	private void onRender3D(Render3DEvent event) {
		if (aura.getValue() && is32k(mc.player.getHeldItemMainhand()) && target != null && mode.getValue()
			.equalsIgnoreCase("onRender")) {
			if (timer.passed(delay.getValue() * 1000) && mc.player.getDistance(target) <= rangeAttack.getValue()) {
				mc.playerController.attackEntity(mc.player, target);
				mc.player.swingArm(EnumHand.MAIN_HAND);
				timer.reset();
			}
		}
	}

}
