package me.robeart.raion.client.module.render

import me.robeart.raion.client.events.events.render.Render2DEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.GLUProjection
import me.robeart.raion.client.util.minecraft.GLUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.awt.Color
import java.util.LinkedList


/**
 * @author cookiedragon234 30/Mar/2020
 */
object PlayerEspModule: Module("Player ESP", "Surround players with a box", Category.RENDER) {
	private val camera = Frustum()
	
	@Listener
	fun onRender2D(event: Render2DEvent) {
		val blend = GL11.glGetBoolean(GL11.GL_BLEND)
		
		GL11.glPushMatrix()
		
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glDisable(GL11.GL_TEXTURE_2D)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GL11.glEnable(GL11.GL_LINE_SMOOTH)
		GL11.glLineWidth(1f)
		
		//noinspection unchecked
		players@ for (player in mc.world.playerEntities) {
			if (player == mc.renderViewEntity) continue
			val projectionList: MutableList<GLUProjection.Projection> = LinkedList()
			val playerBB = player.entityBoundingBox.expand(0.1, 0.1, 0.1)
				.offset(
					-(player.posX - player.lastTickPosX) * (1 - event.partialTicks),
					-(player.posY - player.lastTickPosY) * (1 - event.partialTicks),
					-(player.posZ - player.lastTickPosZ) * (1 - event.partialTicks)
				)
				.offset(
					-mc.renderManager.viewerPosX,
					-mc.renderManager.viewerPosY,
					-mc.renderManager.viewerPosZ
				)
			var x = playerBB.minX
			while (x <= playerBB.maxX) {
				var y = playerBB.minY
				while (y <= playerBB.maxY) {
					var z = playerBB.minZ
					while (z <= playerBB.maxZ) {
						val projection = GLUProjection.getInstance()
							.project(x, y, z, GLUProjection.ClampMode.NONE, false)
						if (projection.isType(GLUProjection.Projection.Type.INVERTED)) {
							println("Inverted")
							continue@players
						}
						projectionList.add(projection)
						z += (playerBB.maxZ - playerBB.minZ) / 2.0
					}
					y += (playerBB.maxY - playerBB.minY) / 2.0
				}
				x += (playerBB.maxX - playerBB.minX) / 2.0
			}
			if (projectionList.isNotEmpty()) {
				val xCoordinates = projectionList.map { p: GLUProjection.Projection -> p.x }
				val minX: Double = xCoordinates.min()!!
				val maxX: Double = xCoordinates.max()!!
				val yCoordinates = projectionList.map { p: GLUProjection.Projection -> p.y }
				val minY: Double = yCoordinates.min()!!
				val maxY: Double = yCoordinates.max()!!
				GLUtils.drawRect(minX.toFloat(), minY.toFloat(), maxX.toFloat(), maxY.toFloat(), Color.ORANGE.rgb)
				println("Rendered")
			} else {
				println("Empty")
			}
		}
		
		GL11.glDisable(GL11.GL_LINE_SMOOTH)
		if (blend) {
			GlStateManager.enableBlend()
			GL11.glEnable(GL11.GL_BLEND)
		} else {
			GlStateManager.disableBlend()
			GL11.glDisable(GL11.GL_BLEND)
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D)
		
		GL11.glPopMatrix()
	}
}

operator fun Vec3d.component1() = this.x
operator fun Vec3d.component2() = this.y
operator fun Vec3d.component3() = this.z
