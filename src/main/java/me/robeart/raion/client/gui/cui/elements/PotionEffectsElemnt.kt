package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion
import net.minecraft.util.math.Vec2f
import java.util.*

/**
 * @author Robeart 16/07/2020
 */
class PotionEffectsElemnt: CuiElement() {

    val color by ValueDelegate(ListValue("Color", "Default", Arrays.asList("Default", "Cui")))

    override fun render(mousePos: Vec2f) {
        super.render(mousePos)

        if (shouldRender()) {
            val list = ArrayList<StringInfo>()
            var width = 0f
            if (mc.player.activePotionEffects.isEmpty() && mc.currentScreen is CuiManagerGui) {
                val text = "Example Effect${ChatFormatting.WHITE} " + "xx:xx"
                width = font.getStringWidth(text)
                list.add(StringInfo(text, -1))
            } else {
                for (effect in mc.player.activePotionEffects) {
                    val potion = effect.potion
                    val number = if (effect.amplifier > 0) " " + (effect.amplifier + 1) else ""
                    val text = "${I18n.format(potion.name)}$number${ChatFormatting.WHITE} ${Potion.getPotionDurationString(effect, 1.0f)}"
                    if (width < font.getStringWidth(text)) width = font.getStringWidth(text)
                    list.add(StringInfo(text, if (color.equals("Default", ignoreCase = true)) effect.potion.liquidColor else cui.getColor()))
                }
            }
            list.sortBy { font.getStringWidth(it.text) }
            if (top) list.reverse()
            var y = 0f
            for (info in list) {
                var x = position.posX + 1
                if (!left && width != font.getStringWidth(info.text)) {
                    x += (width - font.getStringWidth(info.text))
                }
                font.drawString(info.text, x, position.posY + y, info.color)
                y += font.getStringHeight(info.text)
            }
            if (!top && y != position.sizeY) position.posY = (position.posY + position.sizeY) - y
            if (!left && (width + 2) != position.sizeX) position.posX = (position.posX + position.sizeX) - (width + 2)
            position.sizeX = width + 2
            position.sizeY = y
        }
    }

    internal class StringInfo(var text: String, var color: Int)
}