package me.robeart.raion.client.module.render

import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.imixin.IEntityRenderer
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.module.render.search.SearchRenderer
import me.robeart.raion.client.util.*
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.awt.Color


/**
 * @author cookiedragon234 27/Mar/2020
 */
object TracersModule: Module("Tracers", "Draws lines to player entities", Category.RENDER) {
	private val friends by ValueDelegate(BooleanValue("Friends", true))
	private val feet by ValueDelegate(BooleanValue("Feet", false))
	private val maxDistance by ValueDelegate(IntValue("Max Distance", 100, 10, 100, 10))
	private val width by ValueDelegate(FloatValue("Width", 2f, 1f, 4f, 0.5f))
	
	@Listener
	fun onRender(event: Render3DEvent) {
		if (mc.player == null) return
		
		// If there are entities in the world
		if (mc.world.loadedEntityList.isNotEmpty()) {
			val viewEntity = SearchRenderer.mc.renderViewEntity ?: return
			val localEyes: Vec3d = Vec3d(0.0,0.0,1.0)
				.rotatePitch((-Math.toRadians(viewEntity.rotationPitch.toDouble())).toFloat())
				.rotateYaw((-Math.toRadians(viewEntity.rotationYaw.toDouble())).toFloat())
				.add(viewEntity.getPositionEyes(event.partialTicks))
			
			// We need to disable view bobbing to ensure smoothness
			val viewBobbing = mc.gameSettings.viewBobbing
			if (viewBobbing) {
				mc.gameSettings.viewBobbing = false
				// This repositions the camera to remove the view bobbing affect
				(mc.entityRenderer as IEntityRenderer).setupCameraTransform0(event.partialTicks, 0)
			}
			
			// Setup gl states
			GlStateManager.pushMatrix()
			RenderUtils.beginRender()
			GlStateManager.disableDepth()
			GlStateManager.depthMask(false)
			GlStateManager.shadeModel(GL11.GL_SMOOTH)
			GlStateManager.glLineWidth(width)
			GlStateManager.disableTexture2D()
			GL11.glEnable(GL11.GL_LINE_SMOOTH)
			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
			
			for (entity in mc.world.loadedEntityList) {
				if (entity is EntityPlayer && entity !== mc.renderViewEntity) {
					// Get the distance in meters to the enemy
					val distance = mc.player.getDistance(entity)
					if (distance > maxDistance) continue
					
					// Find the percentage of the players distance, use 50 as the maximum
					// Since we dont want a percentage higher than 100, we cap it at 1.0
					val percentage = if (distance > 50) 1f else (distance / 50f)
					
					// Interpolate between red and green using the percentage
					// This means players 50 or more blocks away will be green, 0 will be red
					var colour = MathUtils.getBlendedColor(percentage)
					
					if (Raion.INSTANCE.friendManager.isFriend(entity)) {
						if (!friends) continue
						
						// Render friends as blue
						colour = Color.BLUE
					}
					
					val enemyPos = (
					if (feet)
						entity.positionVector
					else
						entity.getPositionEyes(event.partialTicks)
					)
					
					draw (translate = true, glMode = GL11.GL_LINE_STRIP, format = DefaultVertexFormats.POSITION_COLOR) {
						vertex {
							pos(localEyes)
							color(colour)
						}
						vertex {
							pos(enemyPos)
							color(colour)
						}
					}
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
