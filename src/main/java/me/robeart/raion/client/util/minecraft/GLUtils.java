package me.robeart.raion.client.util.minecraft;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.imixin.IRenderManager;
import me.robeart.raion.client.module.render.HudModule;
import me.robeart.raion.client.util.GLUProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by halalaboos.
 */
public class GLUtils {
	
	private static final Random random = new Random();
	private static final Tessellator tessellator = Tessellator.getInstance();
	public static List<Integer> vbos = new ArrayList<>();
	private static HudModule hudModule = null;
	
	public static void glScissor(int[] rect) {
		glScissor(rect[0], rect[1], rect[0] + rect[2], rect[1] + rect[3]);
	}
	
	public static void glScissor(float x, float y, float x1, float y1) {
		int scaleFactor = getScaleFactor();
		GL11.glScissor((int) (x * scaleFactor), (int) (Minecraft.getMinecraft().displayHeight - (y1 * scaleFactor)), (int) ((x1 - x) * scaleFactor), (int) ((y1 - y) * scaleFactor));
	}
	
	/**
	 * @return The scale factor used by the play's screen gui scale
	 */
	public static int getScaleFactor() {
		int scaleFactor = 1;
		boolean isUnicode = Minecraft.getMinecraft().isUnicode();
		int guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
		
		if (guiScale == 0) guiScale = 1000;
		
		while (scaleFactor < guiScale && Minecraft.getMinecraft().displayWidth / (scaleFactor + 1) >= 320 && Minecraft.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
			scaleFactor++;
		}
		
		if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1)
			scaleFactor--;
		
		return scaleFactor;
		
	}
	
	/**
	 * @return Mouse X cord.
	 */
	public static int getMouseX() {
		return (Mouse.getX() * getScreenWidth() / Minecraft.getMinecraft().displayWidth);
	}
	
	/**
	 * @return Mouse Y cord.
	 */
	public static int getMouseY() {
		return (getScreenHeight() - Mouse.getY() * getScreenHeight() / Minecraft.getMinecraft().displayWidth - 1);
	}
	
	/**
	 * @return Screen width with gui scale.
	 */
	public static int getScreenWidth() {
		return Minecraft.getMinecraft().displayWidth / getScaleFactor();
	}
	
	/**
	 * @return Screen height with gui scale.
	 */
	public static int getScreenHeight() {
		return Minecraft.getMinecraft().displayHeight / getScaleFactor();
	}
	
	/**
	 * Checks if the mouse is hovering over a given item
	 */
	public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
		return (mouseX >= x) && (mouseX <= x + width) && (mouseY >= y) && (mouseY < y + height);
	}
	
	/**
	 * Checks if the mouse is hovering over a given item
	 */
	public static boolean isHovered(float x, float y, float width, float height, float mouseX, float mouseY) {
		return (mouseX >= x) && (mouseX <= x + width) && (mouseY >= y) && (mouseY < y + height);
	}
	
	public static int genVBO() {
		int id = GL15.glGenBuffers();
		vbos.add(id);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
		return id;
	}
	
	/**
	 * Cleans ups the arrays on close
	 */
	public static void cleanup() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
	}
	
	/**
	 * Rect
	 */
	public static void drawBorderRect(float x, float y, float x1, float y1, float borderSize, int color) {
		drawBorder(borderSize, x, y, x1, y1);
		drawRect(x, y, x1, y1, color);
	}
	
	public static void drawBorder(float size, float x, float y, float x1, float y1) {
		glLineWidth(size);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION);
		vertexBuffer.pos(x, y, 0F).endVertex();
		vertexBuffer.pos(x, y1, 0F).endVertex();
		vertexBuffer.pos(x1, y1, 0F).endVertex();
		vertexBuffer.pos(x1, y, 0F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
	}
	
	public static void drawBorder(float size, float x, float y, float x1, float y1, int r, int g, int b, int a) {
		glLineWidth(size);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		vertexBuffer.pos(x, y, 0F).color(r, g, b, a).endVertex();
		vertexBuffer.pos(x, y1, 0F).color(r, g, b, a).endVertex();
		vertexBuffer.pos(x1, y1, 0F).color(r, g, b, a).endVertex();
		vertexBuffer.pos(x1, y, 0F).color(r, g, b, a).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
	}
	
	public static void drawRect(Number x, Number y, Number w, Number h, int color) {
		drawRect(x.floatValue(), y.floatValue(), w.floatValue(), h.floatValue(), color);
	}
	
	public static void drawRect(float x, float y, float w, float h, int color) {
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;
		
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		//glColor(color);
		vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		vertexBuffer.pos((double) x + w, y, 0).color(red, green, blue, alpha).endVertex();
		vertexBuffer.pos(x, y, 0).color(red, green, blue, alpha).endVertex();
		vertexBuffer.pos(x, (double) y + h, 0).color(red, green, blue, alpha).endVertex();
		vertexBuffer.pos((double) x + w, (double) y + h, 0).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}
	
	public static void drawRect(float x, float y, float w, float h, Color c) {
		//System.out.println(c.toString());
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		GlStateManager.color(255f / 255f, 32f / 255f, 0f / 255f, 255f / 255f);
		vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
		vertexBuffer.pos((double) x + w, y, 0).endVertex();
		vertexBuffer.pos(x, y, 0).endVertex();
		vertexBuffer.pos(x, (double) y + h, 0).endVertex();
		vertexBuffer.pos((double) x + w, (double) y + h, 0).endVertex();
		tessellator.draw();
		glColor(1, 1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}
	
	public static void drawRectXCentered(float x, float y, float w, float h, int color) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		glColor(color);
		vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
		vertexBuffer.pos((double) x + (w / 2), y, 0).endVertex();
		vertexBuffer.pos(x - (w / 2), y, 0).endVertex();
		vertexBuffer.pos(x - (w / 2), (double) y + h, 0).endVertex();
		vertexBuffer.pos((double) x + (w / 2), (double) y + h, 0).endVertex();
		tessellator.draw();
		glColor(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
	}
	
	public static void drawGradientRect(int x, int y, int w, int h, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double) x + w, y, 0).color(f1, f2, f3, f).endVertex();
		vertexbuffer.pos(x, y, 0).color(f1, f2, f3, f).endVertex();
		vertexbuffer.pos(x, (double) y + h, 0).color(f5, f6, f7, f4).endVertex();
		vertexbuffer.pos((double) x + w, (double) y + h, 0).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
	
	public static void enableGL2D() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
	}
	
	public static void disableGL2D() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
	}
	
	/**
	 * Colors
	 */
	
	public static void glColor(float red, float green, float blue, float alpha) {
		GlStateManager.color(red, green, blue, alpha);
	}
	
	public static void glColor(Color color) {
		GlStateManager.color((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color
			.getAlpha() / 255F);
	}
	
	public static void glColor(int color) {
		GlStateManager.color((float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, (float) (color >> 24 & 255) / 255F);
	}
	
	public static Color getHSBColor(float hue, float sturation, float luminance) {
		return Color.getHSBColor(hue, sturation, luminance);
	}
	
	public static int getColor(float alpha) {
		int a = (int) (255 * (alpha / 100.0f));
		Color c = getColor();
		int color = ((a & 0xFF) << 24) |
			((c.getRed() & 0xFF) << 16) |
			((c.getGreen() & 0xFF) << 8) |
			((c.getBlue() & 0xFF) << 0);
		return color;
	}
	
	public static float[] convertBounds(Entity e, float partialTicks, int width, int height) {
		float x = -1;
		float y = -1;
		float w = width + 1;
		float h = height + 1;
		
		final Vec3d pos = MinecraftUtils.getInterpolatedAmount(e, partialTicks);
		
		AxisAlignedBB bb = e.getEntityBoundingBox();
		
		if (e instanceof EntityEnderCrystal) {
			bb = new AxisAlignedBB(bb.minX + 0.3f, bb.minY + 0.2f, bb.minZ + 0.3f, bb.maxX - 0.3f, bb.maxY, bb.maxZ - 0.3f);
		}
		
		if (e instanceof EntityItem) {
			bb = new AxisAlignedBB(bb.minX, bb.minY + 0.7f, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
		}
		
		bb = bb.expand(0.15f, 0.1f, 0.15f);
		
		Frustum camera = new Frustum();
		camera.setPosition(Minecraft.getMinecraft().getRenderViewEntity().posX, Minecraft.getMinecraft()
			.getRenderViewEntity().posY, Minecraft.getMinecraft().getRenderViewEntity().posZ);
		
		if (!camera.isBoundingBoxInFrustum(bb)) {
			return null;
		}
		
		final Vec3d[] corners = {
			new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
			new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
			new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),
			new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),
			
			new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
			new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
			new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2),
			new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2)
		};
		
		for (Vec3d vec : corners) {
			final GLUProjection.Projection projection = GLUProjection.getInstance()
				.project(pos.x + vec.x - Minecraft.getMinecraft()
					.getRenderManager().viewerPosX, pos.y + vec.y - Minecraft.getMinecraft()
					.getRenderManager().viewerPosY, pos.z + vec.z - Minecraft.getMinecraft()
					.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, false);
			
			if (projection == null) {
				return null;
			}
			
			x = Math.max(x, (float) projection.getX());
			y = Math.max(y, (float) projection.getY());
			
			w = Math.min(w, (float) projection.getX());
			h = Math.min(h, (float) projection.getY());
		}
		
		if (x != -1 && y != -1 && w != width + 1 && h != height + 1) {
			return new float[]{x, y, w, h};
		}
		
		return null;
	}
	
	public static Color getColor() {
		if (hudModule == null) {
			hudModule = (HudModule) Raion.INSTANCE.getModuleManager().getModule(HudModule.class);
		}
		if (hudModule.nightMode.getValue()) {
			return new Color(hudModule.nightRed.getValue(), hudModule.nightGreen.getValue(), hudModule.nightBlue.getValue());
		}
		if (hudModule.color.getValue().equalsIgnoreCase("Rainbow")) {
			double rainBowColor = ((System.currentTimeMillis()) / (double) hudModule.delayrainbow.getValue()) % 360;
			return Color.getHSBColor((float) (rainBowColor / 360F), hudModule.saturation.getValue(), hudModule.lightness
				.getValue());
		}
		else {
			return new Color(hudModule.red.getValue(), hudModule.green.getValue(), hudModule.blue.getValue());
		}
	}
	
	public static Color getRandomColor(int saturationRandom, float luminance) {
		final float hue = random.nextFloat();
		final float saturation = (random.nextInt(saturationRandom) + (float) saturationRandom) / (float) saturationRandom + (float) saturationRandom;
		return getHSBColor(hue, saturation, luminance);
	}
	
	public static Color getRandomColor() {
		return getRandomColor(1000, 0.6f);
	}
	
	public static void drawCompleteImage(int posX, int posY, int width, int height) {
		drawCompleteImage((float) posX, (float) posY, (float) width, (float) height);
	}
	
	public static void drawCompleteImage(float posX, float posY, float width, float height) {
		GL11.glPushMatrix();
		GL11.glTranslatef(posX, posY, 0.0F);
		GL11.glBegin(7);
		GL11.glTexCoord2f(0.0F, 0.0F);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex3f(0.0F, height, 0.0F);
		GL11.glTexCoord2f(1.0F, 1.0F);
		GL11.glVertex3f(width, height, 0.0F);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex3f(width, 0.0F, 0.0F);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	
	public static void circleESP(BlockPos blockPos, double radius, int color, int mode, float width) {
		double x = blockPos.getX() + 0.5 - ((IRenderManager) Minecraft.getMinecraft()
			.getRenderManager()).getRenderPosX();
		double y = blockPos.getY() - ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosY();
		double z = blockPos.getZ() + 0.5 - ((IRenderManager) Minecraft.getMinecraft()
			.getRenderManager()).getRenderPosZ();
		GL11.glPushMatrix();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(width);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDepthMask(false);
		GL11.glColor4f((float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, (float) (color >> 24 & 255) / 255F);
		GL11.glBegin(mode);
		for (int i = 0; i <= 360; ++i) {
			GL11.glVertex3d(x + Math.sin(i * 3.141592653589793 / 180.0) * radius, y, z + Math.cos(i * 3.141592653589793 / 180.0) * radius);
		}
		GL11.glEnd();
		GL11.glLineWidth(2.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glColor4f(1f, 1f, 1f, 1f);
	}
	
	public static void circleESP(double posX, double posY, double posZ, double radius, int color, int mode, float width) {
		double x = posX - ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosX();
		double y = posY - ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosY();
		double z = posZ - ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosZ();
		GlStateManager.pushMatrix();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(width);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDepthMask(false);
		GL11.glColor4f((float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, (float) (color >> 24 & 255) / 255F);
		GL11.glBegin(mode);
		for (int i = 0; i <= 360; ++i) {
			GL11.glVertex3d(x + Math.sin(i * 3.141592653589793 / 180.0) * radius, y, z + Math.cos(i * 3.141592653589793 / 180.0) * radius);
		}
		GL11.glEnd();
		GL11.glLineWidth(2.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GlStateManager.popMatrix();
	}
	
	public static void GuiRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}
		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(f, f1, f2, f3);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(left, bottom, 0.0D).endVertex();
		bufferbuilder.pos(right, bottom, 0.0D).endVertex();
		bufferbuilder.pos(right, top, 0.0D).endVertex();
		bufferbuilder.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	
}
