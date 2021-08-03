package me.robeart.raion.client.module.render

import me.robeart.raion.client.module.Module
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import org.lwjgl.util.glu.Project

/**
 * @author cookiedragon234 16/Jul/2020
 */
object ViewportModule: Module("Viewport", "Modify your viewport", Category.RENDER) {
	private val hands by ValueDelegate(BooleanValue("Hands", true))
	private val fovSetting = BooleanValue("FOV", true)
	private val fov by ValueDelegate(IntValue("FOV", 90, 10, 180, 5, fovSetting))
	private val aspectSetting = BooleanValue("Aspect Ratio", true)
	private val aspect by ValueDelegate(FloatValue("Aspect Ratio", 1.77f, 0.75f, 2.5f, 0.1f, aspectSetting))
	
	@JvmOverloads
	fun project(oldFovY: Float, oldAspectRatio: Float, oldZNear: Float, oldZFar: Float, fromHands: Boolean = false) {
		if (this.state && (!fromHands || hands)) {
			Project.gluPerspective(
				if (fovSetting.value) fov.toFloat() else oldFovY,
				if (aspectSetting.value) aspect else oldAspectRatio,
				oldZNear,
				oldZFar
			)
		} else {
			Project.gluPerspective(oldFovY, oldAspectRatio, oldZNear, oldZFar)
		}
	}
}
