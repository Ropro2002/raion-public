package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.Utils
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f

/**
 * @author Robeart 22/07/2020
 */
class DirectionsElement: CuiElement() {
    override fun render(mousePos: Vec2f) {
        super.render(mousePos)
        var entity = mc.renderViewEntity
        if (entity == null) entity = mc.player

        val pitch = MathUtils.round(MathHelper.wrapDegrees(entity!!.rotationPitch), 1)
        val yaw = MathUtils.round(MathHelper.wrapDegrees(entity!!.rotationYaw), 1)
        val direction = when(entity.horizontalFacing.getName()) {
            "south" -> "South +Z"
            "north" -> "North -Z"
            "east" -> "East +X"
            "west" -> "West -X"
            else -> "Error"
        }

        val text = "$direction ${ChatFormatting.GRAY}[${ChatFormatting.WHITE}$yaw, $pitch${ChatFormatting.GRAY}]"
        drawText(text, Utils.getRgb(255, 255, 255, 255))
    }
}