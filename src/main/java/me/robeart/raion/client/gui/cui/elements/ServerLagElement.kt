package me.robeart.raion.client.gui.cui.elements

import com.mojang.realmsclient.gui.ChatFormatting
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.gui.cui.CuiManagerGui
import me.robeart.raion.client.gui.cui.element.CuiElement
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.util.math.Vec2f
import java.text.DecimalFormat

/**
 * @author Robeart 22/07/2020
 */
class ServerLagElement: CuiElement() {

    val responseDelay by ValueDelegate(IntValue("Lag Delay", 1000, 0, 10000, 250))

    private var now = System.currentTimeMillis()

    override fun render(mousePos: Vec2f) {
        super.render(mousePos)
        if(System.currentTimeMillis() - now >= responseDelay) {
            val lag = DecimalFormat("0.0").format((System.currentTimeMillis() - now) / 1000.0)
            val text = "${ChatFormatting.GRAY}Server has stopped responding for ${lag}s"
            drawText(text, Utils.getRgb(255, 255, 255, 255))
        } else if(mc.currentScreen is CuiManagerGui) {
            val text = "${ChatFormatting.GRAY}Server has stopped responding for XXXs"
            drawText(text, Utils.getRgb(255, 255, 255, 255))
        }
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        now = System.currentTimeMillis()
    }
}