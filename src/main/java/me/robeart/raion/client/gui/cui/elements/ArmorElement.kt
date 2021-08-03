package me.robeart.raion.client.gui.cui.elements

import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.imixin.IMinecraft
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec2f
import java.util.*

/**
 * @author Robeart 16/07/2020
 */
class ArmorElement : CuiElement() {

    val mode by ValueDelegate(ListValue("Mode", "Horizontal", Arrays.asList("Horizontal", "Vertical")))

    private val presetArmor = Arrays.asList(Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET)

    override fun render(mousePos: Vec2f) {
        super.render(mousePos)
        if (shouldRender()) {
            val renderItem = mc.renderItem
            var x = position.posX.toInt() + 1
            var y = position.posY.toInt()

            for (index in 3 downTo 0) {
                var stack: ItemStack?

                if(mc.currentScreen is CuiManagerGui) stack = presetArmor[index].defaultInstance
                else stack = mc.player.inventory.armorItemInSlot(index)

                stack?: continue

                renderItem.renderItemIntoGUI(stack, x, y)
                renderItem.renderItemOverlays(mc.fontRenderer, stack, x, y)
                if (mode.equals("Horizontal", ignoreCase = true)) x += 17 else y += 17
            }
            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.disableLighting()
            GlStateManager.disableCull()
        }

        val width = if (mode.equals("Horizontal", ignoreCase = true)) 72f else 17f
        val height = if (mode.equals("Horizontal", ignoreCase = true)) 17f else 72f
        position.sizeX = width
        position.sizeY = height
    }
}