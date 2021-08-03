package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.client.ToggleEvent;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.minecraft.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cats
 * credit to seppuku for the idea of using a frustrum to determine if it ought to render, I never would have thought of it
 */

public class NewChunksModule extends Module {
	
	private ICamera frustum = new Frustum();
	
	private Set<ChunkPos> chunks = new HashSet<>();
	
	public NewChunksModule() {
		super("NewChunks", "Marks chunks that are new", Category.RENDER);
	}
	
	@Listener
	public void onReceive(PacketReceiveEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (event.getPacket() instanceof SPacketChunkData) {
			final SPacketChunkData packet = (SPacketChunkData) event.getPacket();
			
			//TODO make it find the opposite array of chunks, because packet.isFullChunk() somehow is flagged for everything
			if (packet.isFullChunk()) return;
			
			final ChunkPos newChunk = new ChunkPos(packet.getChunkX(), packet.getChunkZ());
			this.chunks.add(newChunk);
		}
	}
	
	@Listener
	public void onRender(Render3DEvent event) {
		if (mc.getRenderViewEntity() == null) return;
		this.frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
		
		GlStateManager.pushMatrix();
		RenderUtils.beginRender();
		GlStateManager.disableTexture2D();
		GlStateManager.disableAlpha();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.glLineWidth(2f);
		
		for (ChunkPos chunk : this.chunks) {
			final AxisAlignedBB chunkBox = new AxisAlignedBB(chunk.getXStart(), 0, chunk.getZStart(), chunk.getXEnd(), 0, chunk.getZEnd());
			
			
			GlStateManager.pushMatrix();
			if (this.frustum.isBoundingBoxInFrustum(chunkBox)) {
				double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double) event.getPartialTicks();
				double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double) event.getPartialTicks();
				double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double) event.getPartialTicks();
				RenderUtils.drawBoundingBox(chunkBox.offset(-x, -y, -z), 214, 86,/*red, green,*/147, 100);
			}
			
			GlStateManager.popMatrix();
		}
		
		GlStateManager.glLineWidth(1f);
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
		RenderUtils.endRender();
		GlStateManager.popMatrix();
	}
	
	@Listener
	public void onToggle(ToggleEvent event) {
		if (event.getModule() == this) {
			chunks.clear();
		}
	}
}
