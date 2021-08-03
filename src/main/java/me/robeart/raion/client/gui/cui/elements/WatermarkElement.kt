package me.robeart.raion.client.gui.cui.elements

import me.robeart.raion.client.Raion
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec2f
import java.util.*

/**
 * @author Robeart 1/07/2020
 */
class WatermarkElement: CuiElement() {

    val mode by ValueDelegate(ListValue("Mode", "Both", Arrays.asList("Both", "Logo", "Text")))

    private val logo = ResourceLocation("textures/gui/logo.png")

    override fun render(mousePos: Vec2f) {
        super.render(mousePos)
        val text = "Raion v" + Raion.VERSION
        if (shouldRender()) {
            when(mode) {
                "Both" -> {
                    font.drawString(text, this.position.posX + font.getStringWidth(text) + 1, this.position.posY + 1, cui.getColor())
                    mc.textureManager.bindTexture(logo)
                    GLUtils.drawCompleteImage(this.position.posX, this.position.posY, font.getStringWidth(text), font.getStringWidth(text))
                }
                "Logo" -> {
                    GLUtils.glColor(cui.getColor())
                    mc.textureManager.bindTexture(logo)
                    GLUtils.drawCompleteImage(this.position.posX, this.position.posY, font.getStringWidth(text), font.getStringWidth(text))
                }
                "Text" -> {
                    font.drawString(text, this.position.posX + 1, this.position.posY + 1, cui.getColor())
                }
            }
        }
        when(mode) {
            "Both" -> {
                position.sizeX = font.getStringWidth(text) * 2
                position.sizeY = font.getStringWidth(text)
            }
            "Logo" -> {
                position.sizeX = font.getStringWidth(text)
                position.sizeY = font.getStringWidth(text)
            }
            "Text" -> {
                position.sizeX = font.getStringWidth(text)
                position.sizeY = font.getStringHeight(text)
            }
        }
    }
}
