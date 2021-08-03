package me.robeart.raion.client.util

import me.robeart.raion.client.gui.cui.utils.Box2f
import me.robeart.raion.client.util.GLUProjection.Projection
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.culling.ClippingHelperImpl
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min


/**
 * @author cookiedragon234 09/May/2020
 */
object FrustumUtils {
	private fun dot(
		p_178624_1_: FloatArray,
		p_178624_2_: Double,
		p_178624_4_: Double,
		p_178624_6_: Double
	): Double {
		return p_178624_1_[0].toDouble() * p_178624_2_ + p_178624_1_[1].toDouble() * p_178624_4_ + p_178624_1_[2].toDouble() * p_178624_6_ + p_178624_1_[3].toDouble()
	}
	
	fun getScreenCoords(vec3d: Vec3d): Vec2f? {
		val helper = ClippingHelperImpl.getInstance()
		
		val x = vec3d.x
		val y = vec3d.y
		val z = vec3d.z
		
		for (i in 0 until 6) {
			val afloat: FloatArray = helper.frustum[i]
			
			val dot = this.dot(afloat, x, y, z) <= 0.0
			println("Dot $i : $dot")
			if (dot) {
				return Vec2f(afloat[0] * x.toFloat(), afloat[1] * y.toFloat()).also {
					println("Vec2f(${it.x}, ${it.y})")
				}
			}
		}
		return null
	}
	
	val mc = Minecraft.getMinecraft()
	
	fun getEntity2dBox(e: Entity, partialTicks: Float, scaledResolution: ScaledResolution): Box2f? {
		val pos = e.getInterpolatedPos()
		val bb = e.entityBoundingBox
		
		var x = -1f
		var y = -1f
		var w = -1f
		var h = -1f
		
		val corners = arrayOf(
			Vec3d(bb.minX - bb.maxX + e.width / 2, 0.0, bb.minZ - bb.maxZ + e.width / 2),
			Vec3d(bb.maxX - bb.minX - e.width / 2, 0.0, bb.minZ - bb.maxZ + e.width / 2),
			Vec3d(bb.minX - bb.maxX + e.width / 2, 0.0, bb.maxZ - bb.minZ - e.width / 2),
			Vec3d(bb.maxX - bb.minX - e.width / 2, 0.0, bb.maxZ - bb.minZ - e.width / 2),
			Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
			Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
			Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2),
			Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2)
		)
		
		val maxWidth = scaledResolution.scaledWidth
		val maxHeight = scaledResolution.scaledHeight
		
		for (corner in corners) {
			val projection = GLUProjection.getInstance()
				.project(
					pos.x + corner.x - mc.renderManager.viewerPosX,
					pos.y + corner.y - mc.renderManager.viewerPosY,
					pos.z + corner.z - mc.renderManager.viewerPosZ,
					GLUProjection.ClampMode.NONE,
					false
				) ?: continue
			
			if (projection == Projection.Type.FAIL) continue
			
			x = max(x, projection.x.toFloat())
			y = max(y, projection.y.toFloat())
			w = min(w, projection.x.toFloat())
			h = min(h, projection.y.toFloat())
			
			if (
				(x >= 0f && x <= maxWidth)
				&&
				(y >= 0f && y <= maxHeight)
				&&
				(w >= 0f && w <= maxWidth)
				&&
				(h >= 0f && h <= maxHeight)
			) {
				return Box2f(x, y, w, h)
			}
		}
		return null
	}
	
	//fun get2DFrom3D(x: Double, y: Double, z: Double): Vec2f? = get2DFrom3D(x.toFloat(), y.toFloat(), z.toFloat())
	
	/*fun get2DFrom3D(x: Float, y: Float, z: Float): Vec2f? {
		val mc = Minecraft.getMinecraft()
		GL11.glPushAttrib(GL11.GL_TRANSFORM_BIT)
		
		GL11.glMatrixMode(GL11.GL_PROJECTION)
		GL11.glPushMatrix()
		GL11.glMatrixMode(GL11.GL_MODELVIEW)
		GL11.glPushMatrix()
		
		try {
			(mc.entityRenderer as MixinEntityRenderer).setupCameraTransform0( 1f, 0)
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
		val modelMatrix = BufferUtils.createFloatBuffer(16)
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix)
		
		val projMatrix = BufferUtils.createFloatBuffer(16)
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projMatrix)
		
		val viewport = BufferUtils.createIntBuffer(16)
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW)
		GL11.glPopMatrix()
		GL11.glMatrixMode(GL11.GL_PROJECTION)
		GL11.glPopMatrix()
		
		GL11.glPopAttrib()
		
		val screen2D = BufferUtils.createFloatBuffer(16)
		return if (Project.gluProject(x, y, z, modelMatrix, projMatrix, viewport, screen2D)) {
			Vec2f(screen2D[0], mc.displayHeight - screen2D[1]).also {
				System.out.printf(
					"Convert [ %6.2f %6.2f %6.2f ] -> Screen [ %6.2f %6.2f ]\n",
					x, y, z, it.x, it.y
				)
			}
		} else null
	}*/
	
	/*fun get2DFrom3D(x: Float, y: Float, z: Float): Vec2f? {
		val screenCoords = BufferUtils.createFloatBuffer(4)
		val viewport = BufferUtils.createIntBuffer(16)
		val modelView = BufferUtils.createFloatBuffer(16)
		val projection = BufferUtils.createFloatBuffer(16)
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView)
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)
		val result = GLU.gluProject(x, y, z, modelView, projection, viewport, screenCoords)
		return if (result) {
			Vec2f(screenCoords[0], screenCoords[1]).also {
				System.out.printf(
					"Convert [ %6.2f %6.2f %6.2f ] -> Screen [ %6.2f %6.2f ]\n",
					x, y, z, it.x, it.y
				)
			}
		} else null
	}*/
	
	/*fun get2DFrom3D(x: Float, y: Float, z: Float): Vec2f? {
		val screen_coords = GLAllocation.createDirectFloatBuffer(4)
		val viewport = GLAllocation.createDirectIntBuffer(16)
		val modelview = GLAllocation.createDirectFloatBuffer(16)
		val projection = GLAllocation.createDirectFloatBuffer(16)
		
		GL11.glGetFloat(2982 /*GL_MODELVIEW_MATRIX*/, modelview)
		GL11.glGetFloat(2983 /*GL_PROJECTION_MATRIX*/, projection)
		GL11.glGetInteger(2978 /*GL_VIEWPORT*/, viewport)
		val result = GLU.gluProject(x, y, z, modelview, projection, viewport, screen_coords)
		return if (result) {
			//System.out.printf("Convert [ %6.2f %6.2f %6.2f ] -> Screen [ %4d %4d ]\n", x, y, z, (int)screen_coords[0], (int)(screen_coords[3] - screen_coords[1]));
			System.out.printf(
				"Convert [ %6.2f %6.2f %6.2f ] -> Screen [ %4d %4d ]\n",
				x, y, z, screen_coords[0].toInt(), (screen_coords[3] - screen_coords[1]).toInt()
			)
			Vec2f(screen_coords[0], screen_coords[3] - screen_coords[1])
		} else {
			System.out.printf("Failed to convert 3D coords to 2D screen coords")
			null
		}
	}*/
}
