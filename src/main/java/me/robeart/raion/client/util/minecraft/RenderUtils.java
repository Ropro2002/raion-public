package me.robeart.raion.client.util.minecraft;

import me.robeart.raion.client.imixin.IRenderManager;
import me.robeart.raion.client.util.KUtilsKt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Collections;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final Tessellator tessellator = Tessellator.getInstance();
	
	public static void drawLine(Vec3d startPos, Vec3d endPos, int color, boolean smooth, float width) {
		drawLine(startPos, endPos, color);
	}
	
	public static void drawLine(Vec3d startPos, Vec3d endPos, int color) {
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		drawLine(startPos, endPos, red, green, blue, alpha);
	}
	
	public static void drawLine(Vec3d startPos, BlockPos endPos, float red, float green, float blue, float alpha, double x, double y, double z) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.setTranslation(x, y, z);
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(startPos.x, startPos.y, startPos.z).color(red, green, blue, alpha).endVertex();
		buffer.pos(endPos.getX() + 0.5, endPos.getY() + 0.5, endPos.getZ() + 0.5).color(red, green, blue, alpha).endVertex();
		buffer.setTranslation(0,0,0);
		tessellator.draw();
	}
	
	public static void drawLine(Vec3d startPos, Vec3d endPos, float red, float green, float blue, float alpha, double x, double y, double z) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.setTranslation(x, y, z);
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(startPos.x, startPos.y, startPos.z).color(red, green, blue, alpha).endVertex();
		buffer.pos(endPos.x, endPos.y, endPos.z).color(red, green, blue, alpha).endVertex();
		buffer.setTranslation(0,0,0);
		tessellator.draw();
	}
	
	public static void drawLine(Vec3d startPos, Vec3d endPos, float red, float green, float blue, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(startPos.x, startPos.y, startPos.z).color(red, green, blue, alpha).endVertex();
		buffer.pos(endPos.x, endPos.y, endPos.z).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
	}
	
	public static void draw2DESP(EntityLivingBase entity, float width, int color) {
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		double x = (bb.maxX - bb.minX) / 2;
		double y = (bb.maxY - bb.minY) / 2;
		Vec3d vec3d = KUtilsKt.getInterpolatedPos(entity);
		GlStateManager.pushMatrix();
		GlStateManager.translate(vec3d.x, vec3d.y + y, vec3d.z);
		GlStateManager.glNormal3f(0f, 0f, 0f);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0f, 1f, 0f);
		GlStateManager.scale(2.2d, 1.5d, 1.5d);
		beginRender();
		GlStateManager.disableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
		
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		glColor(color);
		glLineWidth(width);
		bufferBuilder.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION);
		bufferBuilder.pos(-x, -y, 0).endVertex();
		bufferBuilder.pos(-x, y, 0).endVertex();
		bufferBuilder.pos(x, y, 0).endVertex();
		bufferBuilder.pos(x, -y, 0).endVertex();
		tessellator.draw();
		
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		
		GlStateManager.enableDepth();
		endRender();
		GlStateManager.popMatrix();
		
	}
	
	public static void drawRect(float x, float y, float x2, float y2, int color) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		glColor(color);
		bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION);
		bufferBuilder.pos(x2, y, 0).endVertex();
		bufferBuilder.pos(x, y, 0).endVertex();
		bufferBuilder.pos(x, y2, 0).endVertex();
		bufferBuilder.pos(x2, y2, 0).endVertex();
		tessellator.draw();
		
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		
		
	}
	
	public static void glColor(int color) {
		GlStateManager.color((float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, (float) (color >> 24 & 255) / 255F);
	}
	
	public static void bufferAxis(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha, double x, double y, double z) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.setTranslation(x, y, z);
		bufferAxis(tessellator, buffer, axisalignedbb, red, green, blue, alpha);
	}
	public static void bufferAxis(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		bufferAxis(tessellator, buffer, axisalignedbb, red, green, blue, alpha);
	}
	public static void bufferAxis(Tessellator tessellator, BufferBuilder buffer, AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
			.color(red, green, blue, alpha)
			.endVertex();
		buffer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
			.color(red, green, blue, alpha)
			.endVertex();
		buffer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
			.color(red, green, blue, alpha)
			.endVertex();
		buffer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
			.color(red, green, blue, alpha)
			.endVertex();
		buffer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
			.color(red, green, blue, alpha)
			.endVertex();
		buffer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
			.color(red, green, blue, alpha)
			.endVertex();
		buffer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
			.color(red, green, blue, alpha)
			.endVertex();
		buffer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
			.color(red, green, blue, alpha)
			.endVertex();
		tessellator.draw();
	}
	
	public static void vertexBB(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha, double x, double y, double z) {
		Tessellator ts = Tessellator.getInstance();
		BufferBuilder vb = ts.getBuffer();
		vb.setTranslation(x, y, z);
		vertexBB(ts, vb, axisalignedbb, red, green, blue, alpha);
		vb.setTranslation(0, 0, 0);
	}
	
	public static void vertexBB(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
		Tessellator ts = Tessellator.getInstance();
		BufferBuilder vb = ts.getBuffer();
		vertexBB(ts, vb, axisalignedbb, red, green, blue, alpha);
	}
	
	public static void vertexBB(AxisAlignedBB axisalignedbb, int red, int green, int blue, int alpha) {
		Tessellator ts = Tessellator.getInstance();
		BufferBuilder vb = ts.getBuffer();
		vertexBB(ts, vb, axisalignedbb, red, green, blue, alpha);
	}
	
	public static void vertexBB(Tessellator ts, BufferBuilder vb, AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
		vertexBB(ts, vb, axisalignedbb, (int)(red * 255), (int)(green * 255), (int)(blue * 255), (int)(alpha * 255));
	}
	
	public static void vertexBB(Tessellator ts, BufferBuilder vb, AxisAlignedBB axisalignedbb, int red, int green, int blue, int alpha) {
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts X.
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		ts.draw();
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		ts.draw();// Ends X.
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts Y.
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		ts.draw();
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		ts.draw();// Ends Y.
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts Z.
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		ts.draw();
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
		ts.draw();// Ends Z.
	}
	
	public static void blockEsp(Iterable<BlockPos> blockPoses, float red, float green, float blue, float alpha, double length, double length2, double length3) {
		beginRender();
		GlStateManager.disableTexture2D();
		GlStateManager.disableAlpha();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		
		double renderX = ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosX();
		double renderY = ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosY();
		double renderZ = ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosZ();
		GlStateManager.color(red, green, blue, alpha);
		
		for (BlockPos blockPos : blockPoses) {
			GlStateManager.pushMatrix();
			
			double x = blockPos.getX() - renderX;
			double y = blockPos.getY() - renderY;
			double z = blockPos.getZ() - renderZ;
			
			AxisAlignedBB axisalignedbb = new AxisAlignedBB(x, y, z, x + length2, y + length3, z + length);
			vertexBB(axisalignedbb, red, green, blue, alpha);
			
			GlStateManager.popMatrix();
		}
		
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
		endRender();
	}
	
	public static void blockEsp(BlockPos blockPos, int c, double length, double length2) {
		float red = (float) (c >> 16 & 255) / 255.0F;
		float green = (float) (c >> 8 & 255) / 255.0F;
		float blue = (float) (c & 255) / 255.0F;
		float alpha = (float) (c >> 24 & 255) / 255.0F;
		
		blockEsp(Collections.singleton(blockPos), red, green, blue, alpha, length, length2, 1.0);
	}
	
	public static void blockEspHeight(BlockPos blockPos, int c, double height) {
		float red = (float) (c >> 16 & 255) / 255.0F;
		float green = (float) (c >> 8 & 255) / 255.0F;
		float blue = (float) (c & 255) / 255.0F;
		float alpha = (float) (c >> 24 & 255) / 255.0F;
		
		blockEsp(Collections.singleton(blockPos), red, green, blue, alpha, 1, 1, height);
	}
	
	public static void drawSelectionBoundingBox(RayTraceResult result, float width, int c) {
		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			
			Minecraft mc = Minecraft.getMinecraft();
			float red = (float) (c >> 16 & 255) / 255.0F;
			float green = (float) (c >> 8 & 255) / 255.0F;
			float blue = (float) (c & 255) / 255.0F;
			float alpha = (float) (c >> 24 & 255) / 255.0F;
			
			beginRender();
			GlStateManager.glLineWidth(width);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			BlockPos blockpos = result.getBlockPos();
			IBlockState iblockstate = Minecraft.getMinecraft().world.getBlockState(blockpos);
			
			if (iblockstate.getMaterial() != Material.AIR) {
				AxisAlignedBB boundingBox = iblockstate.getSelectedBoundingBox(mc.world, blockpos)
					.grow(0.0020000000949949026D)
					.offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
				drawBoundingBox(boundingBox, red, green, blue, alpha);
			}
			
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			endRender();
			
		}
	}
	
	public static void drawEntityBoundingBox(Entity entity, int c, float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		
		float red = (float) (c >> 16 & 255) / 255.0F;
		float green = (float) (c >> 8 & 255) / 255.0F;
		float blue = (float) (c & 255) / 255.0F;
		float alpha = (float) (c >> 24 & 255) / 255.0F;
		
		GlStateManager.pushMatrix();
		beginRender();
		GlStateManager.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		
		if (entity != mc.player) {
			
			// This is used to get the position of the entity for partial tick rendering
			// So you use the position that they should be rendering at, instead of the entity position without care for partial ticks in rendering
			final Vec3d pos = KUtilsKt.getInterpolatedPos(entity);
			
			final AxisAlignedBB entityBox = entity.getEntityBoundingBox();
			final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
				entityBox.minX - entity.posX + pos.x,
				entityBox.minY - entity.posY + pos.y,
				entityBox.minZ - entity.posZ + pos.z,
				entityBox.maxX - entity.posX + pos.x,
				entityBox.maxY - entity.posY + pos.y,
				entityBox.maxZ - entity.posZ + pos.z
			);
			drawBoundingBox(axisAlignedBB.grow(0.0020000000949949026D), red, green, blue, alpha);
		}
		
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		endRender();
		GlStateManager.popMatrix();
	}
	
	public static void drawBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha) {
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, 0.0F).endVertex();
		tessellator.draw();
	}
	
	public static float getNametagSize(double x, double y, double z) {
		double scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
		double twoDscale = scaleFactor / (scaleFactor * scaleFactor);
		return (float) twoDscale + ((float) Minecraft.getMinecraft().player.getDistance(x, y, z) / 13);
	}
	
	//Note: use these functions for render modules, the idea is to mainly catch most mistakes that could cause render issues
	
	/**
	 * Prepares for the use of GL, resetting color and other things, hopefully fixing the grey box issue
	 * Idea came from Kami's prepareGL()
	 */
	public static void beginRender() {
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.resetColor();
	}
	
	/**
	 * ends the use of GL, to make sure that everything was reset properly
	 * Also came from Kami
	 */
	public static void endRender() {
		GlStateManager.resetColor();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
	}
	
}
