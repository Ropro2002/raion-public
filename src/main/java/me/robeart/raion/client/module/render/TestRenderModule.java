package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.events.render.Render2DEvent;
import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class TestRenderModule extends Module {
	
	public TestRenderModule() {
		super("TestRender", "Test", Category.RENDER);
	}
	
	@Listener
	public void onRender2D(Render2DEvent event) {
	
	}
	
	public void drawNameplate(EntityLivingBase e, float viewerYaw, float viewerPitch, int c, float partialTicks, boolean isThirdPersonFrontal) {
		Minecraft mc = Minecraft.getMinecraft();
		float red = (float) (c >> 16 & 255) / 255.0F;
		float green = (float) (c >> 8 & 255) / 255.0F;
		float blue = (float) (c & 255) / 255.0F;
		float alpha = (float) (c >> 24 & 255) / 255.0F;
		//GlStateManager.pushMatrix();
		//GlStateManager.translate(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ);
		//GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
		//GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		
		if (e != mc.player) {
			double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double) partialTicks;
			double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double) partialTicks;
			double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double) partialTicks;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			//drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
			buffer.pos(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ)
				.color(red, green, blue, alpha)
				.endVertex();
			buffer.pos(e.posX, e.posY + (e.height / 2), e.posZ).color(red, green, blue, alpha).endVertex();
			tessellator.draw();
		}
		
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		//GlStateManager.popMatrix();
	}
	
	public float getNametagSize(EntityLivingBase player) {
		ScaledResolution scaledRes = new ScaledResolution(mc);
		double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0D);
		return (float) twoDscale + (mc.player.getDistance(player) / (0.7f * 10));
	}
	
	@Listener
	public void onRender3D(Render3DEvent event) {
		for (Entity e : mc.world.loadedEntityList) {
			if (!(e instanceof EntityLivingBase)) continue;
			drawNameplate((EntityLivingBase) e, mc.getRenderManager().playerViewY, mc.getRenderManager().playerViewX, 0xFFFF0000, event
				.getPartialTicks(), mc.getRenderManager().options.thirdPersonView == 2);
		}
	}
	
}
