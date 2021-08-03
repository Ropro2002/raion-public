package me.robeart.raion.client.module

import me.robeart.raion.client.Raion
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate

/**
 * @author cookiedragon234 07/Jun/2020
 */
object ClickGuiModule: Module("ClickGui", "O", "Enable the ClickGui", Category.RENDER, false) {
	private val backgroundStyleSetting = ListValue("Background", "Blur", listOf("Opaque", "Blur"))
	val backgroundStyle by ValueDelegate(backgroundStyleSetting)
	val backgroundOpacity by ValueDelegate(FloatValue("Opacity", 0.5f, 0.0f, 1.0f, 0.05f, backgroundStyleSetting, "Opaque"))
	val maxCategorySize by ValueDelegate(IntValue("Category Size", 200, 100, 500, 5))
	val scrollSpeed by ValueDelegate(FloatValue("Scroll Speed", 5f, 1f, 10f, 1f))
	val useEventScroll by ValueDelegate(BooleanValue("Event Scroll", false))
	val colourSpeed by ValueDelegate(FloatValue("Colour Interpolate", 0.2f, 0.1f, 1f, 0.05f))
	
	override fun onEnable() {
		mc.displayGuiScreen(Raion.INSTANCE.gui)
		state = false
	}
}
