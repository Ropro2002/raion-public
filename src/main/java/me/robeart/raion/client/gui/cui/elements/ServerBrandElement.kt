package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.Utils
import net.minecraft.util.math.Vec2f

/**
 * @author Robeart 22/07/2020
 */
class ServerBrandElement: CuiElement() {
    override fun render(mousePos: Vec2f) {
        super.render(mousePos)

        val brand = mc.player.serverBrand
        val text = "${ChatFormatting.GRAY}Brand ${ChatFormatting.WHITE}$brand"
        drawText(text, Utils.getRgb(255, 255, 255, 255))
    }
}