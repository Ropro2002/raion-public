package me.robeart.raion.client.gui.cui.values

import me.robeart.raion.client.gui.cui.utils.Box2f
import me.robeart.raion.client.util.MouseButton
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.value.Value
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec2f

/**
 * @author cookiedragon234 16/Jun/2020
 */
abstract class ValueRenderer<T: Value<*>>(
	val value: T,
	val font: MinecraftFontRenderer
) {
	val mc = Minecraft.getMinecraft()
	val position = Box2f()
	var visible = false
	abstract fun render(mousePos: Vec2f)
	abstract fun onMouseDown(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean
	abstract fun onMouseRelease(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean
	abstract fun onMouseMove(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean
	
	fun mouseOver(mousePos: Vec2f) = visible && position.contains(mousePos)
}
