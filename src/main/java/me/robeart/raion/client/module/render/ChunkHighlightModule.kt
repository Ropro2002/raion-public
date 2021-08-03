package me.robeart.raion.client.module.render

import me.robeart.raion.client.module.Module
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.culling.ICamera

/**
 * @author cookiedragon234 21/May/2020
 */
object ChunkHighlightModule: Module("ChunkHighlight", "Highlight loaded chunk boundary", Category.RENDER) {
	private val frustum: ICamera = Frustum()
	
	/*@Listener
	fun onRender(event: Render3DEvent) {
		if (mc.renderViewEntity == null) return
		this.frustum.setPosition(
			mc.renderViewEntity!!.posX,
			mc.renderViewEntity!!.posY,
			mc.renderViewEntity!!.posZ
		)
		mc.world.chunkProvider.loadChunk()
		val chunkBox = AxisAlignedBB(
			chunk.x.toDouble(), 0,
			chunk.z.toDouble(),
			(chunk.x + 16).toDouble(), 0,
			(chunk.z + 16).toDouble()
		)
		GlStateManager.pushMatrix()
		RenderUtils.beginRender()
		GlStateManager.glLineWidth(2.0f)
		GlStateManager.disableTexture2D()
		GlStateManager.disableDepth()
		GlStateManager.depthMask(false)
		if (this.frustum.isBoundingBoxInFrustum(chunkBox)) {
			//TODO make it change color based on chunk difficulty, needs earlier todo in order to work :/
			//RenderUtils.blockEsp(new BlockPos(chunk.x, 0, chunk.z), 0x459d03fc, 16, 16);
			//int red = (int) (mc.world.getDifficultyForLocation(new BlockPos(chunk.x, mc.player.posY, chunk.z)).getAdditionalDifficulty() * 50);
			//int green = 255 - red;
			val x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.partialTicks
				.toDouble()
			val y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.partialTicks
				.toDouble()
			val z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.partialTicks
				.toDouble()
			RenderUtils.drawBoundingBox(chunkBox.offset(-x, -y, -z), 214f, 86f, 147f, 100f)
		}
		RenderUtils.endRender()
		GlStateManager.depthMask(true)
		GlStateManager.enableTexture2D()
		GlStateManager.enableDepth()
		GlStateManager.popMatrix()
	}*/
}
