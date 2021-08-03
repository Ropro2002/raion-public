package me.robeart.raion.client.module.player

import me.robeart.raion.client.events.events.player.OnUpdateEvent
import me.robeart.raion.client.imixin.IEntity
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraftforge.client.GuiIngameForge
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 28/Apr/2020
 */
object BetterPortalsModule: Module("BetterPortals", "Remove unwanted portal functionality", Category.PLAYER) {
	val allowGuis by ValueDelegate(BooleanValue("Allow Gui", true))
	val noRenderEffect by ValueDelegate(BooleanValue("No Render Effect", true))
	val noHitbox by ValueDelegate(BooleanValue("No Hitbox", true))
	val noParticles by ValueDelegate(BooleanValue("No Particles", true))
	val noSound by ValueDelegate(BooleanValue("No Sound", true))

	private var renderPortal: Boolean? = null

	override fun onEnable() {
		renderPortal = GuiIngameForge.renderPortal
	}

	@Listener
	private fun onUpdate(event: OnUpdateEvent) {
		if (allowGuis) {
			(mc.player as IEntity).setInPortal(false)
		}
		if (noRenderEffect) {
			GuiIngameForge.renderPortal = false
		}
	}

	override fun onDisable() {
		renderPortal?.let { GuiIngameForge.renderPortal = it }
	}
}
