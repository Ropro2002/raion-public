package me.robeart.raion.client.module.render

import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.events.events.render.RenderNameEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.module.player.FreecamModule
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.Utils
import me.robeart.raion.client.util.getInterpolatedPos
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper.ceil
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.awt.Color
import java.util.ArrayList


/**
 * @author Robeart
 */
object NametagsModule: Module("NameTags", "Renders a different nameplate above someones head", Category.RENDER) {
	
	val armor by ValueDelegate(BooleanValue("Armor", true))
	val invisible by ValueDelegate(BooleanValue("Invisibles", true))
	val friend by ValueDelegate(BooleanValue("Friend", true))
	val ping by ValueDelegate(BooleanValue("Ping", true))
	val health by ValueDelegate(BooleanValue("HP", true))
	val healthbar by ValueDelegate(BooleanValue("HP Bar", true))
	val scaling by ValueDelegate(FloatValue("Scale", 0.005f, 0f, 0.01f, 0.001f))
	
	@Listener
	fun onRenderName(event: RenderNameEvent) {
		if (shouldRenderEntity(event.entity)) event.isCanceled = true
	}
	
	@Listener
	fun on3DRender(event: Render3DEvent?) {
		for (player in mc.world.loadedEntityList) {
			if (!shouldRenderEntity(player)) continue
			drawNameplate(player as EntityPlayer)
		}
	}
	
	private fun drawNameplate(entity: EntityPlayer) {
		val name = entity.displayName.formattedText
		val ping = if (ping) " " + (mc.player.connection.getPlayerInfo(entity.name)?.responseTime
			?: 0).toString() + "ms" else ""
		val health = MathUtils.clamp(ceil(entity.health), 0, 20)
		val hp = if (this.health) " " + ceil(entity.health + entity.absorptionAmount) else ""
		
		val color = MathUtils.getBlendedColor(health / 20f)
		val c = Utils.getRgb(color.red, color.green, color.blue, color.alpha)
		
		val renderManager = mc.renderManager
		val isThirdPersonFrontal = mc.renderManager.options.thirdPersonView == 2
		
		val pos = entity.getInterpolatedPos()
		
		var distance = FreecamModule.getActiveEntity()!!.getDistance(entity.posX, entity.posY, entity.posZ)
		if (distance < 8.0)
			distance = 4.0
		val scale = .025f + scaling * distance
		
		GlStateManager.pushMatrix()
		RenderUtils.beginRender()
		GlStateManager.disableAlpha()
		val sneak = if (entity.isSneaking) .25f else 0f
		GlStateManager.translate(pos.x, pos.y + entity.eyeHeight + 0.5f - sneak, pos.z)
		GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
		GlStateManager.rotate(renderManager.playerViewX, (if (isThirdPersonFrontal) -1 else 1).toFloat(), 0.0f, 0.0f)
		GlStateManager.scale(-scale, -scale, scale)
		GlStateManager.disableDepth()
		val width = Raion.fontRenderer.getStringWidth("$name$ping$hp")
		val height = Raion.fontRenderer.getStringHeight("$name$ping$hp")
		GLUtils.drawRect(-width / 2f - .5f, if (healthbar) 0f else -.5f, width + 2f, -height, 0x59000000)
		Raion.fontRenderer.drawString(
			"$name$ping",
			-(width / 2),
			-height - 1,
			if (Raion.INSTANCE.friendManager.isFriend(entity) && friend) Color.CYAN.rgb else -1,
			false
		)
		if (this.healthbar) GLUtils.drawRect(-width / 2f - .5f, -.5f, ((width + 2f) / 20f) * health.toFloat(), .5f, c)
		if (this.health) Raion.fontRenderer.drawString(
			hp,
			-(width / 2) + Raion.fontRenderer.getStringWidth("$name$ping"),
			-height - 1,
			c,
			false
		)
		
		//equiqment render
		if (armor) {
			val equipment = ArrayList<ItemStack>()
			equipment.add(entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND))
			equipment.add(entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD))
			equipment.add(entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST))
			equipment.add(entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS))
			equipment.add(entity.getItemStackFromSlot(EntityEquipmentSlot.FEET))
			equipment.add(entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND))
			var x = 0
			val start = -(equipment.size * 8)
			for (itemStack in equipment) {
				if (itemStack == ItemStack.EMPTY) continue
				renderItem(itemStack, start + x, -30)
				x += 16
			}
		}
		
		GlStateManager.enableDepth()
		GlStateManager.enableAlpha()
		RenderUtils.endRender()
		GlStateManager.popMatrix()
	}
	
	val module by lazy { Raion.INSTANCE.moduleManager.getModule(ESPModule.javaClass) as ESPModule }
	fun shouldRenderEntity(entity: Entity): Boolean {
		if ((module.state && !module.renderNameTags) || (!invisible && entity.isInvisible) || entity == mc.renderViewEntity || entity !is EntityPlayer) return false
		return true
	}
	
	fun renderItem(itemStack: ItemStack, x: Int, y: Int) {
		GlStateManager.pushMatrix()
		GlStateManager.scale(0.5f, 0.5f, 0.5f)
		GlStateManager.depthMask(true)
		GlStateManager.clear(256)
		RenderHelper.enableStandardItemLighting()
		mc.renderItem.zLevel = -150f
		GlStateManager.disableAlpha()
		GlStateManager.enableDepth()
		GlStateManager.disableCull()
		mc.renderItem.renderItemAndEffectIntoGUI(itemStack, x, y)
		mc.renderItem.renderItemOverlays(mc.fontRenderer, itemStack, x, y)
		mc.renderItem.zLevel = 0.0f
		RenderHelper.disableStandardItemLighting()
		GlStateManager.enableCull()
		GlStateManager.enableAlpha()
		//GlStateManager.scale(0.5f, 0.5f, 0.5f)
		//GlStateManager.disableDepth()
		//do enchantment rendering
		//GlStateManager.enableDepth()
		GlStateManager.scale(2.0f, 2.0f, 2.0f)
		GlStateManager.popMatrix()
	}
	
}

