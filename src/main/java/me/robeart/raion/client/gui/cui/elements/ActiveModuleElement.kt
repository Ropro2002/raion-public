package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.Raion
import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.module.combat.CrystalAura2
import me.robeart.raion.client.util.ChatUtils
import me.robeart.raion.client.util.Interpolation
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.util.math.Vec2f
import java.util.*

/**
 * @author Robeart 20/07/2020
 */
class ActiveModuleElement: CuiElement() {

    val moduleSlideSpeed by ValueDelegate(FloatValue("Slide Speed", 0.25f, 0.1f, 1f, 0.05f))

    override fun render(mousePos: Vec2f) {
        super.render(mousePos)

        if (shouldRender()) {
            val list = ArrayList<ModuleInfo>()
            var width = 0f
            for(module in Raion.INSTANCE.moduleManager.moduleList) {
                if((!module.state && module.currentWidth == module.desiredWidth) || !module.visible) continue
                module.recalculateWidth(font, moduleSlideSpeed)
                var name = module.name
                var info = module.hudInfo
                if (info != null) name += " ${ChatFormatting.GRAY}$info"
                if(width < module.currentWidth) width = module.currentWidth
                list.add(ModuleInfo(name, module))
            }
            if(mc.currentScreen == CuiManagerGui && list.isEmpty()) list.add(ModuleInfo("Example Module", cui))
            list.sortBy { font.getStringWidth(it.text) }
            if (top) list.reverse()
            var y = 0f
            for (info in list) {
                var x = position.posX + 1
                if (!left && width != font.getStringWidth(info.text)) {
                    x += (width - font.getStringWidth(info.text))
                }
                if(!Interpolation.isNearlyZero(info.module.currentWidth, 2f)) {
                    val height = font.getStringHeight(info.text)
                    val thisHeight = info.module.calculateCurrentHeight(y, moduleSlideSpeed)
                    val thisWidth = font.getStringWidth(info.text)
                    val xOffset = if(left) x - (thisWidth - info.module.currentWidth) else x + (thisWidth - info.module.currentWidth)

                    font.drawString(info.text, xOffset, position.posY + thisHeight, cui.getColor())

                    y += height
                }
            }
            if (!top && y != position.sizeY) position.posY = (position.posY + position.sizeY) - y
            if (!left && (width + 2) != position.sizeX) position.posX = (position.posX + position.sizeX) - (width + 2)
            position.sizeX = width + 2
            position.sizeY = y
        }
    }

    internal class ModuleInfo(var text: String, var module: Module)
}