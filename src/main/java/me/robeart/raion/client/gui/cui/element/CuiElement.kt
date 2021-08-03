package me.robeart.raion.client.gui.cui.element

import com.google.gson.JsonObject
import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.gui.cui.RaionCui
import me.robeart.raion.client.gui.cui.anchor.AnchorPoint
import me.robeart.raion.client.gui.cui.anchor.AnchorPointDirection
import me.robeart.raion.client.gui.cui.anchor.AnchorPointDirection.BOTTOM_LEFT
import me.robeart.raion.client.gui.cui.anchor.AnchorPointDirection.TOP_LEFT
import me.robeart.raion.client.gui.cui.anchor.ElementAnchorPoint
import me.robeart.raion.client.gui.cui.utils.Box2f
import me.robeart.raion.client.gui.cui.values.ValueRenderer
import me.robeart.raion.client.module.render.CuiModule
import me.robeart.raion.client.util.*
import me.robeart.raion.client.util.font.Fonts
import me.robeart.raion.client.util.font.MinecraftFontRenderer
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.value.Value
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.world.BossInfo
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.reflect.Modifier
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * @author cookiedragon234 16/Jun/2020
 */
abstract class CuiElement: Serializable {
	protected val mc = Minecraft.getMinecraft()
	protected val font = Fonts.font35
	protected val cui = Raion.INSTANCE.moduleManager.getModule(CuiModule::class.java) as CuiModule
	
	/**
	 * Unique UUID to represent this element
	 */
	var uuid = UUID.randomUUID()
	/**
	 * Position is stored in the aspect ratio of 1920:1080 but is scaled according to the actual minecraft resolution
	 */
	var position: Box2f = Box2f()
	var visible = true
	var dragging = false
	var dragOffset: Vec2f? = null
	var anchor: AnchorPoint? = null
	
	private var desiredDragX: Float = -1f
	private var desiredDragY: Float = -1f

	val left: Boolean get() = position.posX + (position.sizeX / 2) < (RaionCui.width / 4)
	val top: Boolean get() = position.posY + (position.sizeY / 2) < (RaionCui.height / 4)
	
	var x: Number
		get() = position.posX
		set(value) { position.posX = value.toFloat() }
	var y: Number
		get() = position.posY
		set(value) { position.posY = value.toFloat() }
	var width: Number
		get() = position.sizeX
		set(value) { position.sizeX = value.toFloat() }
	var height: Number
		get() = position.sizeY
		set(value) { position.sizeY = value.toFloat() }
	
	val values by lazy {
		val values = TreeMap<Value<*>, ValueRenderer<*>>(Comparator.comparing(Value<*>::getName))
		
		val valuesSet = hashSetOf<Value<*>>()
		for (field in this::class.java.declaredFields) {
			if (Value::class.java.isAssignableFrom(field.type)) {
				field.isAccessible = true
				val fieldValue = if (Modifier.isStatic(field.modifiers)) field[null] else field[this]
				val value = fieldValue as Value<*>
				valuesSet.add(value)
			} else if (ValueDelegate::class.java.isAssignableFrom(field.type)) {
				field.isAccessible = true
				val fieldValue = if (Modifier.isStatic(field.modifiers)) field[null] else field[this]
				val value = (fieldValue as ValueDelegate<*, *>).value
				valuesSet.add(value)
			}
		}
		for (value in valuesSet) {
			values[value] = value.createRenderer()
		}
		
		values
	}
	
	open fun shouldRender() = visible
	
	private var renderingPass = false
	open fun preRender(mousePos: Vec2f) {
		// Since mouse positions are stored as ints in OpenGL in order to make dragging smooth we need to interpolate the dragging
		// We specify a speed of 1f, which means that it will reach the desired position over 1 full tick (not render tick, game tick)
		// This is also the rate at which mouse positions are updated, meaning that by the time we get a new mouse movement we will
		// have completed the previous mouse movement
		
		var originX = position.posX
		var originY = position.posY
		
		if (desiredDragX != -1f || desiredDragY != -1f) {
			if (desiredDragX != -1f && position.posX != desiredDragX) {
				this.position.posX = Interpolation.finterpTo(this.position.posX, desiredDragX, mc.renderPartialTicks, 1f)
			} else {
				desiredDragX = -1f
			}
			if (desiredDragY != -1f && position.posY != desiredDragY) {
				this.position.posY = Interpolation.finterpTo(this.position.posY, desiredDragY, mc.renderPartialTicks, 1f)
			} else {
				desiredDragY = -1f
			}
		} else {
			val clampedX = MathHelper.clamp(position.posX, 0f + 2f, 957f - position.sizeX)
			val clampedY = MathHelper.clamp(position.posY, 0f + 2f, 505f - position.sizeY)
			this.position.posX = Interpolation.finterpTo(this.position.posX, clampedX, mc.renderPartialTicks, 0.55f)
			this.position.posY = Interpolation.finterpTo(this.position.posY, clampedY, mc.renderPartialTicks, 0.55f)
		}
		
		if (!dragging && originX == position.posX && originY == position.posY) {
			anchor?.let { anchor ->
				if (anchor is ElementAnchorPoint) {
					val back = anchor.snapTo
					val backAnchor = back?.anchor
					if (backAnchor is ElementAnchorPoint) {
						if (backAnchor.snapTo == this ) {
							back.anchor = null
						}
					}
				}
			}
			anchor?.snap(this)
		}
		
		renderingPass = shouldRender()
		if (renderingPass) {
			GlStateManager.pushMatrix()
			GL11.glScissor(position.posX.toInt(), position.posY.toInt(), position.sizeX.toInt(), position.sizeY.toInt())
		}
	}
	
	open fun onUpdate() {}

	open fun onPacketReceive(event: PacketReceiveEvent) {}
	
	open fun render(mousePos: Vec2f) {}
	
	open fun postRender(mousePos: Vec2f) {
		if (renderingPass) {
			GlStateManager.popMatrix()
		}
		if (dragging) {
			GLUtils.drawBorder(1.5f, position.posX, position.posY, position.bottomX, position.bottomY, 255, 255, 255, 120)
			val thisCorners = position.getCorners()
			for (other in RaionCui.elements) {
				if (other == this) continue

				val otherCorners = other.position.getCorners()
				val intersect = other.position.getCornerIntersects(thisCorners, otherCorners, 4f)
				val width = other.position.sizeX / 2
				val x = if(intersect?.x == other.position.posX) intersect.x else other.position.posX + width
				if (intersect != null) {
					GLUtils.drawRect(x, intersect.y, width, 1f, Color.RED.rgb)
				}
			}
		}
		renderingPass = false
	}
	
	open fun onMouseDown(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		if (!consumed && button == MouseButton.LEFT && shouldRender()) {
			dragging = isMouseOver(mousePos)
			if (dragging) {
				dragOffset = this.position.topLeft - mousePos
				anchor = null
				return true
			}
		}
		return false
	}
	open fun onMouseRelease(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		if (dragging) {
			dragging = false
			dragOffset = null
			if (anchor == null) {
				AnchorPoint.processAnchorPoints(this)
			}
			return true
		}
		return false
	}
	open fun onMouseMove(mousePos: Vec2f, button: MouseButton, consumed: Boolean): Boolean {
		if (dragging) {
			this.desiredDragX = mousePos.x + dragOffset!!.x
			this.desiredDragY = mousePos.y + dragOffset!!.y
		}
		return false
	}
	
	
	fun isTextLeft(): Boolean {
		return this.anchor?.direction?.let {
			it == TOP_LEFT || it == BOTTOM_LEFT
		} ?: false
	}
	
	open fun isMouseOver(mousePos: Vec2f): Boolean = this.position.contains(mousePos)

	fun drawText(text: String) = drawText(text, 0xFFFFFF)
	fun drawText(text: String, color: Int) {
		if (shouldRender()) {
			font.drawString(text, this.position.posX + 1, this.position.posY + 1, color)
		}
		val width = font.getStringWidth(text) + 2
		val height = font.getStringHeight(text)
		position.sizeX = width
		position.sizeY = height
	}
	
	override fun read(jsonObject: JsonObject) {
		this.uuid = UUID.fromString(jsonObject.get("uuid").asString)
		this.visible = jsonObject.get("visible").asBoolean
		if (jsonObject.has("anchor")) {
			this.anchor = AnchorPoint.readAnchorPoint(jsonObject.get("anchor").asJsonObject)
		}
		position.read(jsonObject.get("position").asJsonObject)
	}
	
	override fun write(jsonObject: JsonObject) {
		jsonObject.addProperty("uuid", this.uuid.toString())
		jsonObject.addProperty("visible", visible)
		anchor?.let {
			val anchorObj = JsonObject()
			AnchorPoint.writeAnchorPoint(it, anchorObj)
			jsonObject.add("anchor", anchorObj)
		}
		JsonObject().also {
			position.write(it)
			jsonObject.add("position", it)
		}
	}

}
