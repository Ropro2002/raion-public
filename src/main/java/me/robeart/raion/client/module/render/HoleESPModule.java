package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.KUtilsKt;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.util.minecraft.RenderUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.FloatValue;
import me.robeart.raion.client.value.IntValue;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;

/**
 * @author Robeart
 */
public class HoleESPModule extends Module {

	public BooleanValue frustumCheck = new BooleanValue("Check Frustrum", true);
	public FloatValue height = new FloatValue("Render Height", 0.1f, -1f, 1f, 0.05f);
	public IntValue range = new IntValue("Range", 16, 5, 50, 1);
	public IntValue rangey = new IntValue("Y Range", 8, 1, 30, 1);
	public BooleanValue obbHolesSetting = new BooleanValue("Holes Obsidian", true);
	public IntValue red = new IntValue("Red", 0, 0, 255, 1, obbHolesSetting);
	public IntValue green = new IntValue("Green", 100, 0, 255, 1, obbHolesSetting);
	public IntValue blue = new IntValue("Blue", 0, 0, 255, 1, obbHolesSetting);
	public IntValue alpha = new IntValue("Alpha", 170, 0, 255, 1, obbHolesSetting);
	public BooleanValue bedHolesSetting = new BooleanValue("Holes Bed", true);
	public IntValue red2 = new IntValue("Red", 255, 0, 255, 1, bedHolesSetting);
	public IntValue green2 = new IntValue("Green", 100, 0, 255, 1, bedHolesSetting);
	public IntValue blue2 = new IntValue("Blue", 255, 0, 255, 1, bedHolesSetting);
	public IntValue alpha2 = new IntValue("Alpha", 170, 0, 255, 1, bedHolesSetting);
	public IntValue tickDelay = new IntValue("Delay", 10, 0, 40, 1);
	private int ticks;
	private ArrayList<BlockPos> bedrock;
	private ArrayList<BlockPos> obsidian;
	
	private ICamera frustum = new Frustum();

	public HoleESPModule() {
		super("HoleESP", "Allows you to see bedrock or obsidian holes easier", Category.RENDER);
	}

	@Override
	public void moduleLogic() {
		if (ticks >= tickDelay.getValue()) {
			ticks = 0;
			
			// 10 is the default arraylist capacity
			int assumeLengthBedrock = 10;
			int assumeLengthObsidian = 10;
			{
				ArrayList<BlockPos> bedrock = this.bedrock;
				ArrayList<BlockPos> obsidian = this.obsidian;
				// Use the number of bedrock and obsidian we retrieved last tick
				// This means that the arraylist will be less likely to need to resize its capacity
				if (bedrock != null) {
					assumeLengthBedrock = bedrock.size();
				}
				if (obsidian != null) {
					assumeLengthObsidian = obsidian.size();
				}
			}
			
			if (frustumCheck.getValue()) {
				Entity activeEntity = mc.getRenderViewEntity();
				if (activeEntity == null) { return; }
				this.frustum.setPosition(activeEntity.posX, activeEntity.posY, activeEntity.posZ);
			}
			
			ArrayList<BlockPos> newBedrock = new ArrayList<>(assumeLengthBedrock);
			ArrayList<BlockPos> newObsidian = new ArrayList<>(assumeLengthObsidian);
			for (BlockPos.MutableBlockPos pos : MinecraftUtils.getBlocksInRadiusMutable(range.getValue(), rangey.getValue())) {
				if (!frustumCheck.getValue() || frustum.isBoundingBoxInFrustum(new AxisAlignedBB((pos)))) {
					int originX = pos.getX();
					int originY = pos.getY();
					int originZ = pos.getZ();
					int value = isHoleMutable(pos);
					if (value == 2 && bedHolesSetting.getValue()) {
						newBedrock.add(new BlockPos(originX, originY, originZ));
					}
					else if (value == 1 && obbHolesSetting.getValue()) {
						newObsidian.add(new BlockPos(originX, originY, originZ));
					}
				}
			}
			this.bedrock = newBedrock;
			this.obsidian = newObsidian;
		}
		else ticks++;
	}

	public static int isHoleMutable(BlockPos.MutableBlockPos offset) {
		int originX = offset.getX();
		int originY = offset.getY();
		int originZ = offset.getZ();
		try {
			for (int i = 0; i < 3; i++) {
				offset.setPos(originX, originY + i, originZ);
				if (mc.world.getBlockState(offset).getMaterial() != Material.AIR) {
					return 0;
				}
			}

			boolean bedrock = true;
			for (EnumFacing f : EnumFacing.values()) {
				//System.out.println(offset + " " + f);
				if (f == EnumFacing.UP) continue;
				offset = KUtilsKt.offsetMutable(offset, originX, originY, originZ, f);
				Block blockF = mc.world.getBlockState(offset).getBlock();
				if (blockF != Blocks.BEDROCK) {
					if (blockF != Blocks.OBSIDIAN) {
						return 0;
					}
					bedrock = false;
				}
			}
			return bedrock ? 2 : 1;
		} finally {
			offset.setPos(originX, originY, originZ);
		}
	}


	@Listener
	private void onRender3D(Render3DEvent event) {
		ArrayList<BlockPos> bedrock = this.bedrock;
		ArrayList<BlockPos> obsidian = this.obsidian;
		if (bedrock == null || obsidian == null) return;

		RenderUtils.blockEsp(obsidian, red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f, alpha.getValue() / 255f, 1, 1, -height.getValue());
		RenderUtils.blockEsp(bedrock, red2.getValue() / 255f, green2.getValue() / 255f, blue2.getValue() / 255f, alpha2.getValue() / 255f, 1,  1, -height.getValue());
	}

	@Override
	public String getHudInfo() {
		if (obsidian == null || bedrock == null) return "0";
		return String.valueOf(obsidian.size() + bedrock.size());
	}
}
