package me.robeart.raion.client.module.render.search

import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.imixin.IEntityRenderer
import me.robeart.raion.client.module.render.TracersModule
import me.robeart.raion.client.module.render.search.SearchBlocksManager.highlightedBlocks
import me.robeart.raion.client.util.bb
import me.robeart.raion.client.util.draw
import me.robeart.raion.client.util.minecraft.MinecraftUtils
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.client.util.pos
import me.robeart.raion.client.util.vertex
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

/**
 * @author cookiedragon234 19/Jun/2020
 */
object SearchRenderer {
	val mc = Minecraft.getMinecraft()
	
	fun onRender3D(event: Render3DEvent) {
		if (mc.player == null) return
		
		if (highlightedBlocks.isNotEmpty()) {
			val viewEntity = mc.renderViewEntity ?: return
			val localEyes: Vec3d = Vec3d(0.0,0.0,1.0)
				.rotatePitch((-Math.toRadians(viewEntity.rotationPitch.toDouble())).toFloat())
				.rotateYaw((-Math.toRadians(viewEntity.rotationYaw.toDouble())).toFloat())
				.add(viewEntity.getPositionEyes(event.partialTicks))
			
			// We need to disable view bobbing to ensure smoothness
			val viewBobbing = mc.gameSettings.viewBobbing
			if (viewBobbing) {
				mc.gameSettings.viewBobbing = false
				(mc.entityRenderer as IEntityRenderer).setupCameraTransform0(event.partialTicks, 0)
			}
			
			// Setup gl states
			GlStateManager.pushMatrix()
			RenderUtils.beginRender()
			GlStateManager.disableDepth()
			GlStateManager.depthMask(false)
			GlStateManager.shadeModel(GL11.GL_SMOOTH)
			GlStateManager.disableTexture2D()
			GL11.glEnable(GL11.GL_LINE_SMOOTH)
			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
			
			val tRed = SearchModule.tRed
			val tGreen = SearchModule.tGreen
			val tBlue = SearchModule.tBlue
			val tAlpha = SearchModule.tAlpha
			
			for (block in highlightedBlocks) {
				// Get the distance in meters to the enemy
				val distance = mc.player.getDistanceSq(block)
				if (distance > (SearchModule.maxDistance * SearchModule.maxDistance)) continue // square max distance as its cheaper than rooting the distancesq
				
				if (SearchModule.trace.value) {
					GlStateManager.glLineWidth(SearchModule.tWidth)
					draw (translate = true, glMode = GL_LINE_STRIP, format = POSITION_COLOR) {
						vertex {
							pos(localEyes)
							color(tRed, tGreen, tBlue, tAlpha)
						}
						vertex {
							pos(block, 0.5)
							color(tRed, tGreen, tBlue, tAlpha)
						}
					}
				}
				
				if (SearchModule.highlight.value && false) {
					GlStateManager.glLineWidth(SearchModule.hWidth)
					RenderUtils.vertexBB(AxisAlignedBB(block), SearchModule.hRed, SearchModule.hGreen, SearchModule.hBlue, SearchModule.hAlpha)
				}
			}
			
			// Disable gl flags
			GlStateManager.shadeModel(GL11.GL_FLAT)
			GL11.glDisable(GL11.GL_LINE_SMOOTH)
			GlStateManager.enableDepth()
			GlStateManager.depthMask(true)
			RenderUtils.endRender()
			GlStateManager.enableTexture2D()
			GlStateManager.popMatrix()
			
			// Reset view bobbing
			if (viewBobbing) {
				mc.gameSettings.viewBobbing = viewBobbing
				(mc.entityRenderer as IEntityRenderer).setupCameraTransform0(event.partialTicks, 0)
			}
		}
	}
}
