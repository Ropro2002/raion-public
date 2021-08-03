package me.robeart.raion.client.gui.cui

import com.google.common.collect.HashBiMap
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ibxm.Player
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.gui.cui.elements.*
import me.robeart.raion.client.util.MouseButton
import me.robeart.raion.client.util.Saveable
import me.robeart.raion.client.util.forEachReversed
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec2f
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import java.io.File
import java.lang.reflect.Modifier
import java.sql.Time
import java.util.*


/**
 * @author cookiedragon234 02/May/2020
 */
object RaionCui : Saveable {
    override val configFile: File = File("raion/gui.json")
    override val name: String
        get() = "RaionCui"

    private val mc = Minecraft.getMinecraft()
    val elements: MutableList<CuiElement> = LinkedList()
    val availableElements: Map<String, Class<out CuiElement>>
    val elementNameMap: Map<Class<out CuiElement>, String>
    var xScale = 1f
    var yScale = 1f

    val width = 1920f
    val height = 1017f

    fun onUpdate() {
        elements.forEach { it.onUpdate() }
    }

    fun onPacketReceive(event: PacketReceiveEvent) {
        elements.forEach{ it.onPacketReceive(event) }
    }

    fun onRender2D() {
        GlStateManager.pushMatrix()
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.scale(xScale, yScale, 1f)
        val mousePos = Vec2f(Mouse.getX().toFloat() / xScale, Mouse.getY().toFloat() / yScale)
        this.preRender(mousePos)
        this.render(mousePos)
        this.postRender(mousePos)
        GlStateManager.scale(1 / xScale, 1 / yScale, 1f)
        GlStateManager.popMatrix()
    }

    fun onMouseClick(mouseX: Float, mouseY: Float, mouseBtn: Int) {
        val mouseX = mouseX / xScale
        val mouseY = mouseY / yScale
        var consumed = false
        elements.forEachReversed {
            if (it.onMouseDown(Vec2f(mouseX, mouseY), MouseButton.fromState(mouseBtn), consumed)) {
                consumed = true
            }
        }
    }

    fun onMouseRelease(mouseX: Float, mouseY: Float, mouseBtn: Int) {
        val mouseX = mouseX / xScale
        val mouseY = mouseY / yScale
        var consumed = false
        elements.forEachReversed {
            consumed = it.onMouseRelease(Vec2f(mouseX, mouseY), MouseButton.fromState(mouseBtn), consumed)
        }
    }

    fun onMouseMove(mouseX: Float, mouseY: Float, mouseBtn: Int) {
        val mouseX = mouseX / xScale
        val mouseY = mouseY / yScale
        var consumed = false
        elements.forEachReversed {
            consumed = it.onMouseMove(Vec2f(mouseX, mouseY), MouseButton.fromState(mouseBtn), consumed)
        }
    }

    private fun preRender(mousePos: Vec2f) {
        xScale = Display.getWidth() / width
        yScale = Display.getHeight() / height
        elements.forEach { it.preRender(mousePos) }
    }

    private fun render(mousePos: Vec2f) {
        elements.forEach { it.render(mousePos) }
    }

    private fun postRender(mousePos: Vec2f) {
        elements.forEach { it.postRender(mousePos) }
    }

    init {
        HashBiMap.create(
                mapOf(
                        "Available Element Manager" to AvailableElementManager::class.java,
                        "Active Element Manager" to ActiveElementManager::class.java,
                        "FPS Counter" to FPSCounterElement::class.java,
                        "Speed Counter" to SpeedElement::class.java,
                        "Watermark" to WatermarkElement::class.java,
                        "Tps Counter" to TpsElement::class.java,
                        "Armor HUD" to ArmorElement::class.java,
                        "Potion Effects" to PotionEffectsElemnt::class.java,
                        "Inventory Viewer" to InventoryViewerElement::class.java,
                        "Mini Player" to MiniPlayerElement::class.java,
                        "Active Modules" to ActiveModuleElement::class.java,
                        "Directions" to DirectionsElement::class.java,
                        "Coords" to CoordsElement::class.java,
                        "Ping" to PingElement::class.java,
                        "Time" to TimeElement::class.java,
                        "Server Brand" to ServerBrandElement::class.java,
                        "Server Lag" to ServerLagElement::class.java,
                        "Durability" to DurabilityElement::class.java,
                        "Player List" to PlayerListElement::class.java
                )
        ).also { map ->
            availableElements = map
            elementNameMap = map.inverse() as Map<Class<out CuiElement>, String>
        }
    }

    override fun load() {
        super.load()
        if (!elements.contains(AvailableElementManager)) {
            elements += AvailableElementManager
        }
        if (!elements.contains(ActiveElementManager)) {
            elements += ActiveElementManager
        }
    }

    override fun write(jsonObject: JsonObject) {
        jsonObject.add("elements", JsonArray().also { arr ->
            for (element in elements) {
                arr.add(JsonObject().also {
                    it.addProperty("name", elementNameMap[element::class.java])
                    element.write(it)
                })
            }
        })
    }

    override fun read(jsonObject: JsonObject) {
        loop@ for (elementObj in jsonObject.getAsJsonArray("elements").map { it.asJsonObject }) {
            val elementName = elementObj["name"].asString
            val elementClass = availableElements[elementName]
            if (elementClass == null) {
                IllegalArgumentException("Unknown element $elementName").printStackTrace()
                continue
            }
            when (elementClass) {
                AvailableElementManager::class.java -> {
                    AvailableElementManager.read(elementObj)
                    elements += AvailableElementManager
                }
                ActiveElementManager::class.java -> {
                    ActiveElementManager.read(elementObj)
                    elements += ActiveElementManager
                }
                else -> {
                    val instanceF = elementClass.declaredFields.firstOrNull {
                        it.type == elementClass && Modifier.isStatic(it.modifiers)
                    }
                    val element = if (instanceF != null) {
                        instanceF.get(null) as CuiElement
                    } else {
                        elementClass.newInstance()
                    }
                    element.read(elementObj)
                    elements += element
                }
            }
        }
    }
}
