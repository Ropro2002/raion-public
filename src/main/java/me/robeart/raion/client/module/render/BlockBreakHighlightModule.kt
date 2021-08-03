package me.robeart.raion.client.module.render

import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.imixin.IRenderManager
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.util.minecraft.MinecraftUtils
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.mixin.common.render.IMixinRenderGlobal
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.AxisAlignedBB
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 08/Jun/2020
 */
object BlockBreakHighlightModule: Module(
	"BreakHighlight",
	"Highlight blocks that other players are breaking",
	Category.RENDER
) {
	private val damagedBlocks by lazy { (mc.renderGlobal as IMixinRenderGlobal).damagedBlocks }
	
	@Listener
	private fun onRenderWorld(event: Render3DEvent) {
		if (damagedBlocks?.isNotEmpty() != true)
			return
		
		GlStateManager.pushMatrix()
		RenderUtils.beginRender()
		GlStateManager.disableTexture2D()
		GlStateManager.disableAlpha()
		GlStateManager.disableDepth()
		GlStateManager.depthMask(false)
		GlStateManager.glLineWidth(2f)
		
		val renderPos = MinecraftUtils.getInterpolatedPos(mc.renderViewEntity, event.partialTicks.toDouble())
		
		for (value in damagedBlocks.values) {
			val progress = MathUtils.clamp(value.partialBlockDamage, 0, 10) / 10f
			val colour = MathUtils.getBlendedColor(-(progress - 1), 255 / 5)
			GLUtils.glColor(colour)
			val pos = value.position
			val inset = 0.5 * progress
			val aab = AxisAlignedBB(
				pos.x + 0.5 - inset,
				pos.y + 0.5 - inset,
				pos.z + 0.5 - inset,
				pos.x + 0.5 + inset,
				pos.y + 0.5 + inset,
				pos.z + 0.5 + inset
			)
			GlStateManager.pushMatrix()
			
			RenderUtils.vertexBB(aab, colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f, -renderPos.x, -renderPos.y, -renderPos.z)
			
			GlStateManager.popMatrix()
			
			//draw (translate = true, )
		}
		
		GlStateManager.glLineWidth(1f)
		GlStateManager.enableTexture2D()
		GlStateManager.enableDepth()
		GlStateManager.depthMask(true)
		GlStateManager.enableAlpha()
		RenderUtils.endRender()
		GlStateManager.popMatrix()
	}
}
