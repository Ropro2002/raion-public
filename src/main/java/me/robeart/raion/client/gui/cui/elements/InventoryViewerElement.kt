package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.gui.cui.element.CuiElement
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec2f

/**
 * @author Robeart 19/07/2020
 */
class InventoryViewerElement: CuiElement() {
    override fun render(mousePos: Vec2f) {
        super.render(mousePos)

        val width = 154f
        val height = 52f
        val x1 = (6f * 0.00390625f).toDouble()
        val y1 = (82f * 0.00390625f).toDouble()
        val x2 = (169f * 0.00390625f).toDouble()
        val y2 = (137f * 0.00390625f).toDouble()
        if (shouldRender()) {
            val renderItem = mc.renderItem
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(GuiContainer.INVENTORY_BACKGROUND)
            val x: Float = position.posX
            val y: Float = position.posY

            val tessellator = Tessellator.getInstance()
            val bufferbuilder = tessellator.buffer
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferbuilder.pos(x.toDouble(), (y + height).toDouble(), 0.0).tex(x1, y2).endVertex()
            bufferbuilder.pos((x + width).toDouble(), (y + height).toDouble(), 0.0).tex(x2, y2).endVertex()
            bufferbuilder.pos((x + width).toDouble(), y.toDouble(), 0.0).tex(x2, y1).endVertex()
            bufferbuilder.pos(x.toDouble(), y.toDouble(), 0.0).tex(x1, y1).endVertex()
            tessellator.draw()
            var posX = position.posX + 1
            var posY = position.posY + 1
            var j = 0
            for(i in 9..35) {
                val stack = mc.player.inventory.getStackInSlot(i)
                renderItem.renderItemIntoGUI(stack, posX.toInt(), posY.toInt())
                renderItem.renderItemOverlays(mc.fontRenderer, stack, posX.toInt(), posY.toInt())
                posX += 17
                j++
                if(j == 9) {
                    posX = position.posX + 1
                    posY += 17
                    j = 0
                }
            }
            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.disableLighting()
            GlStateManager.disableCull()
        }
        position.sizeX = width
        position.sizeY = height
    }
}