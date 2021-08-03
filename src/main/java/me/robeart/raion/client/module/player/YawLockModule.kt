package me.robeart.raion.client.module.player

import me.robeart.raion.client.events.events.player.OnUpdateEvent
import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.imixin.IRenderManager
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.Interpolation
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import kotlin.math.roundToInt

/**
 * @author cookiedragon234 08/Jun/2020
 */
object YawLockModule: Module("YawLock", "Lock your yaw rotation", Category.PLAYER) {
	val diagonal by ValueDelegate(BooleanValue("Allow Diagonals", true))
	val render by ValueDelegate(BooleanValue("Render Lines", true))
	val interpSpeed by ValueDelegate(FloatValue("Interp Speed", 0.1f, 0f, 5f, 0.1f))

	var ignoreTicks = 0
	var heightProgress = 0f

	@Listener
	fun onRender(event: Render3DEvent) {
		if (mc.player == null) return

		if (mc.mouseHelper.deltaX != 0 || mc.mouseHelper.deltaY != 0 || isAnyMouseButtonDown()) {
			ignoreTicks = 4
		} else {
			val diff = 360 / if (diagonal) 8f else 4f
			if (ignoreTicks <= 0) {
				var yaw = mc.player.rotationYaw + 180
				yaw = (yaw / diff).roundToInt() * diff
				yaw -= 180
				mc.player.prevRotationYaw = mc.player.rotationYaw
				mc.player.rotationYaw = Interpolation.finterpTo(
					mc.player.rotationYaw,
					yaw,
					mc.renderPartialTicks,
					interpSpeed
				)
				mc.player.ridingEntity?.let {
					it.prevRotationYaw = it.rotationYaw
					it.rotationYaw = mc.player.rotationYaw
				}
			} else {
				ignoreTicks -= 1
			}
		}

		if (render && (ignoreTicks > 0 || heightProgress > 0)) {
			val distance = 300.0 // how far away to render

			val root = mc.player.positionVector
			val positions = if (diagonal) {
				arrayOf(
					root.add(distance, 0.0, 0.0),
					root.add(distance / 2, 0.0, distance / 2),
					root.add(0.0, 0.0, distance),
					root.add(-distance / 2, 0.0, distance / 2),
					root.add(-distance, 0.0, 0.0),
					root.add(-distance / 2, 0.0, -distance / 2),
					root.add(0.0, 0.0, -distance),
					root.add(distance / 2, 0.0, -distance / 2)
				)
			} else {
				arrayOf(
					root.add(distance, 0.0, 0.0),
					root.add(0.0, 0.0, distance),
					root.add(-distance, 0.0, 0.0),
					root.add(0.0, 0.0, -distance)
				)
			}

			if (ignoreTicks > 0) {
				heightProgress = Interpolation.finterpTo(heightProgress, 255f, mc.renderPartialTicks, interpSpeed)
			} else if (heightProgress > 0) {
				heightProgress = Interpolation.finterpTo(heightProgress, 0f, mc.renderPartialTicks, interpSpeed)
			}

			if (heightProgress != 0f) {
				GlStateManager.pushMatrix()
				RenderUtils.beginRender()
				GlStateManager.disableDepth()
				GlStateManager.depthMask(false)
				GlStateManager.shadeModel(GL11.GL_SMOOTH)
				GlStateManager.glLineWidth(2f)
				GlStateManager.disableTexture2D()
				GL11.glEnable(GL11.GL_LINE_SMOOTH)
				GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)

				val renderX = (Minecraft.getMinecraft().renderManager as IRenderManager).renderPosX
				val renderY = (Minecraft.getMinecraft().renderManager as IRenderManager).renderPosY
				val renderZ = (Minecraft.getMinecraft().renderManager as IRenderManager).renderPosZ

				GlStateManager.translate(-renderX, -renderY, -renderZ)

				for (position in positions) {
					RenderUtils.drawLine(
						position.subtract(0.0, heightProgress.toDouble(), 0.0),
						position.add(0.0, heightProgress.toDouble(), 0.0),
						0.96f,
						0.19f,
						0.19f,
						((heightProgress / 255f) / 2) + 127.5f
					)
				}
				// Disable gl flags
				GlStateManager.shadeModel(GL11.GL_FLAT)
				GL11.glDisable(GL11.GL_LINE_SMOOTH)
				GlStateManager.enableDepth()
				GlStateManager.depthMask(true)
				RenderUtils.endRender()
				GlStateManager.enableTexture2D()
				GlStateManager.popMatrix()
			}
		}
	}

	@Listener
	fun onUpdate(event: OnUpdateEvent) {
		if (mc.player == null) return


	}

	private fun isAnyMouseButtonDown() = (0 until Mouse.getButtonCount()).any { Mouse.isButtonDown(it) }
}
