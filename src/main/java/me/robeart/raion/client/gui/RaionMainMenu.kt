package me.robeart.raion.client.gui

import me.robeart.raion.client.Raion
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiMainMenu

/**
 * @author cookiedragon234 07/Jun/2020
 */
class RaionMainMenu: GuiMainMenu() {
	override fun initGui() {
		super.initGui()
		val startHeight = height / 4 + 48
		val buttonHeight = 24
		this.buttonList.add(GuiButton(30, width / 2 - 100, startHeight + (buttonHeight * 5), "Raion"))
	}
	
	override fun actionPerformed(button: GuiButton) {
		if (button.id == 30) {
			val gui = Raion.INSTANCE.gui
			gui.setParent(this)
			mc.displayGuiScreen(gui)
			return
		}
		super.actionPerformed(button)
	}
}
