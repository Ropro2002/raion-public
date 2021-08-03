package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.Utils
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec2f

/**
 * @author Robeart 22/07/2020
 */
class DurabilityElement: CuiElement() {
    override fun render(mousePos: Vec2f) {
        super.render(mousePos)
        val item = mc.player.heldItemMainhand
        val color = MathUtils.getBlendedColor((item.maxDamage - item.itemDamage).toFloat() / item.maxDamage.toFloat())
        val c = Utils.getRgb(color.red, color.green, color.blue, color.alpha)
        val text = "${ChatFormatting.GRAY}Durability ${ChatFormatting.RESET}${item.maxDamage - item.itemDamage}"
        if(item.maxDamage > 0) drawText(text, c)
        else if(mc.currentScreen is CuiManagerGui) drawText("${ChatFormatting.GRAY}Durability ${ChatFormatting.WHITE}xx", -1)
    }
}