package me.robeart.raion.client.util.font;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.*;

/**
 * Utility class to assist in rendering. Uses the {@link Tessellator} and {@link GlStateManager} classes provided by Minecraft.
 */
public class FontUtils {
	
	private static final Tessellator tessellator = Tessellator.getInstance();
	public static float zLevel = 0;
	
	private FontUtils() {
	
	}
	
	public static void drawTextureRect(float x, float y, float width, float height, float u, float v, float t, float s) {
		if (false) {
			BufferBuilder renderer = tessellator.getBuffer();
			renderer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
			renderer.pos(x + width, y, zLevel).tex(t, v).endVertex();
			renderer.pos(x, y, zLevel).tex(u, v).endVertex();
			renderer.pos(x, y + height, zLevel).tex(u, s).endVertex();
			renderer.pos(x, y + height, zLevel).tex(u, s).endVertex();
			renderer.pos(x + width, y + height, zLevel).tex(t, s).endVertex();
			renderer.pos(x + width, y, zLevel).tex(t, v).endVertex();
			tessellator.draw();
		} else {
			GlStateManager.glBegin(GL_TRIANGLES);
			
			GlStateManager.glTexCoord2f(t, v);
			GlStateManager.glVertex3f(x + width, y, zLevel);
			
			GlStateManager.glTexCoord2f(u, v);
			GlStateManager.glVertex3f(x, y, zLevel);
			
			GlStateManager.glTexCoord2f(u, s);
			GlStateManager.glVertex3f(x, y + height, zLevel);
			
			GlStateManager.glTexCoord2f(u, s);
			GlStateManager.glVertex3f(x, y + height, zLevel);
			
			GlStateManager.glTexCoord2f(t, s);
			GlStateManager.glVertex3f(x + width, y + height, zLevel);
			
			GlStateManager.glTexCoord2f(t, v);
			GlStateManager.glVertex3f(x + width, y, zLevel);
			
			GlStateManager.glEnd();
		}
	}
	
	/**
	 * Renders a line from the given x, y positions to the second x1, y1 positions.
	 */
	public static void drawLine(float size, float x, float y, float x1, float y1) {
		glLineWidth(size);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(GL_LINES, DefaultVertexFormats.POSITION);
		vertexBuffer.pos(x, y, 0F).endVertex();
		vertexBuffer.pos(x1, y1, 0F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
	}
	
}
