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
class CoordsElement: CuiElement() {
    override fun render(mousePos: Vec2f) {
        super.render(mousePos)
        var entity = mc.renderViewEntity
        if (entity == null) entity = mc.player
        val coordX = MathUtils.round(entity!!.posX, 1)
        val coordY = MathUtils.round(entity!!.posY, 1)
        val coordZ = MathUtils.round(entity!!.posZ, 1)
        val netx = MathUtils.round(coordX / 8, 1)
        val netz = MathUtils.round(coordZ / 8, 1)
        val owx = MathUtils.round(coordX * 8, 1)
        val owz = MathUtils.round(coordZ * 8, 1)

        val pitch = MathUtils.round(MathHelper.wrapDegrees(entity!!.rotationPitch), 1)
        val yaw = MathUtils.round(MathHelper.wrapDegrees(entity!!.rotationYaw), 1)
        val direction = when(entity.horizontalFacing.getName()) {
            "south" -> "South +Z"
            "north" -> "North -Z"
            "east" -> "East +X"
            "west" -> "West -X"
            else -> "Error"
        }
        val coords = when(entity.dimension) {
            -1 -> "$coordX, $coordY, $coordZ ${ChatFormatting.GRAY}[${ChatFormatting.WHITE}$owx, $owz${ChatFormatting.GRAY}]"
            0 -> "$coordX, $coordY, $coordZ ${ChatFormatting.GRAY}[${ChatFormatting.WHITE}$netx, $netz${ChatFormatting.GRAY}]"
            else -> "$coordX, $coordY, $coordZ"
        }
        val text = "${ChatFormatting.GRAY}XYZ ${ChatFormatting.WHITE}$coords"
        drawText(text, Utils.getRgb(255, 255, 255, 255))
    }
}