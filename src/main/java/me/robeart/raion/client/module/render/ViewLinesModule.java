package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.minecraft.RenderUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 */
public class ViewLinesModule extends Module {
	
	public ViewLinesModule() {
		super("ViewFinder", "Draws lines on other players, allowing you to see what they're looking at", Category.RENDER);
	}
	
	@Listener
	public void onRender(Render3DEvent event) {
		
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityPlayer && entity != mc.player) {
				final EntityPlayer player = (EntityPlayer) entity;
				RayTraceResult ray = player.rayTrace(6, mc.getRenderPartialTicks());
				if (ray != null) {
					GlStateManager.pushMatrix();
					RenderUtils.beginRender();
					GlStateManager.glLineWidth(4.0F);
					GlStateManager.disableTexture2D();
					GlStateManager.depthMask(false);
					double x = mc.getRenderManager().viewerPosX; //mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double) event.getPartialTicks();
					double y = mc.getRenderManager().viewerPosY;//mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double) event.getPartialTicks();
					double z = mc.getRenderManager().viewerPosZ; //mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double) event.getPartialTicks();
					//This has been acting weird, I am either doing something very wrong with positions, or the drawLine thing isn't working well.
					RenderUtils.drawLine(player.getPositionEyes(mc.getRenderPartialTicks())
						.subtract(x, y, z), ray.hitVec.subtract(x, y, z)/*new Vec3d(ray.getBlockPos().getX() + 0.5, ray.getBlockPos().getY()+ 0.5, ray.getBlockPos().getZ() + 0.5).subtract(x, y, z)*/, 0xFF42f5d4, true, 2);
					if (player.isSwingInProgress && mc.world.getBlockState(ray.getBlockPos())
						.getMaterial() != Material.AIR) {
						RenderUtils.blockEsp(ray.getBlockPos(), 0x3042f5d4, 1, 1);
					}
					RenderUtils.endRender();
					GlStateManager.enableDepth();
					GlStateManager.depthMask(true);
					GlStateManager.enableTexture2D();
					GlStateManager.popMatrix();
				}
			}
			//Vec3d eyes = player.getPositionEyes(mc.getRenderPartialTicks());
			//RayTraceResult ray = player.rayTrace(6, mc.getRenderPartialTicks());
			//Vec3d viewedBlock = new Vec3d(ray.getBlockPos().getX(), ray.getBlockPos().getY(), ray.getBlockPos().getZ());
			//RenderUtils.drawLine(eyes, viewedBlock, 0x8032CD32, false, 500);
		}
		
	}
}
