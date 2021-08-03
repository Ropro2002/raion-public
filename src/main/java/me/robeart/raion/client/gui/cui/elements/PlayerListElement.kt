package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.Raion
import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.RaionCui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.util.math.Vec2f
import java.awt.Color
import java.util.*

/**
 * @author Robeart 22/07/2020
 */
class PlayerListElement: CuiElement() {

    val friends by ValueDelegate(BooleanValue("Friends Only", false))

    override fun render(mousePos: Vec2f) {
        super.render(mousePos)

        if (shouldRender()) {
            val list = ArrayList<StringInfo>()
            var width = 0f
            for(entity in mc.world.loadedEntityList) {
                if(!(entity is EntityPlayer) || mc.player == entity) continue
                val friend = Raion.INSTANCE.friendManager.isFriend(entity)
                if(friends && !friend) continue
                val c = if(friend) Color.CYAN.rgb else Color.WHITE.rgb
                val d = mc.player.getDistance(entity).toInt()
                val text = "${entity.displayName.formattedText} (${d}m)"
                if (width < font.getStringWidth(text)) width = font.getStringWidth(text)
                list.add(StringInfo(text, c, d))
            }
            if (list.isEmpty() && mc.currentScreen is CuiManagerGui)  {
                font.getStringWidth("Example Player (XXm)")
                list.add(StringInfo("Example Player (XXm)", Color.WHITE.rgb, 0))
            }
            list.sortBy { it.distance }
            //if (top) list.reverse()
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

    internal class StringInfo(val text: String, val color: Int, val distance: Int)
}