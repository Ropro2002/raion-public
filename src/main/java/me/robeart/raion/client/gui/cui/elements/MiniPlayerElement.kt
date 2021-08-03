package me.robeart.raion.client.gui.cui.elements

import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.minecraft.GLUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.Vec2f

/**
 * @author Robeart 19/07/2020
 */
class MiniPlayerElement: CuiElement() {
    override fun render(mousePos: Vec2f) {
        super.render(mousePos)

        val width = 50f
        val height = 65f
        if (shouldRender()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(GuiContainer.INVENTORY_BACKGROUND)
            val x: Float = position.posX
            val y: Float = position.posY
            drawEntityOnScreen(x + width / 2, y + height - 5f, 30, mc.player)
        }
        position.sizeX = width
        position.sizeY = height
    }

    fun drawEntityOnScreen(posX: Float, posY: Float, scale: Int, ent: EntityLivingBase) {
        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GlStateManager.translate(posX, posY, 50.0f)
        GlStateManager.scale((-scale).toFloat(), scale.toFloat(), scale.toFloat())
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
        val f = ent.renderYawOffset
        val f1 = ent.rotationYaw
        val f2 = ent.rotationPitch
        val f3 = ent.prevRotationYawHead
        val f4 = ent.rotationYawHead
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
        RenderHelper.enableStandardItemLighting()
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f)
        ent.renderYawOffset = 0f
        ent.rotationYaw = 0f
        ent.rotationPitch = 0f
        ent.rotationYawHead = ent.rotationYaw
        ent.prevRotationYawHead = ent.rotationYaw
        GlStateManager.translate(0.0f, 0.0f, 0.0f)
        val rendermanager = Minecraft.getMinecraft().renderManager
        rendermanager.setPlayerViewY(180.0f)
        rendermanager.isRenderShadow = false
        rendermanager.renderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
        rendermanager.isRenderShadow = true
        ent.renderYawOffset = f
        ent.rotationYaw = f1
        ent.rotationPitch = f2
        ent.prevRotationYawHead = f3
        ent.rotationYawHead = f4
        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }
}