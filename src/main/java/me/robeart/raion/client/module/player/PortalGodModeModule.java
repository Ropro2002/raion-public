package me.robeart.raion.client.module.player;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.events.events.render.Render2DEvent;
import me.robeart.raion.client.imixin.ISPacketPlayerPosLook;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import me.robeart.raion.client.value.BooleanValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author cats
 */
public class PortalGodModeModule extends Module {
	
	private final MinecraftFontRenderer font = Fonts.INSTANCE.getFont36();
	private boolean cancelPacket;
	private boolean moving;
	private boolean render;
	private BooleanValue auto = new BooleanValue("Automatic", false);
	
	public PortalGodModeModule() {
		super("PortalGodMode", "Prevents damage after going through a portal", Category.PLAYER);
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		if (this.auto.getValue()) {
			if (this.cancelPacket = true) {
				this.cancelPacket = false;
			}
			this.moving = (mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0);
			if (this.moving) render = false;
		}
	}
	
	@Listener
	public void onRenderHotBar(Render2DEvent event) {
		if (this.render) {
			ScaledResolution res = new ScaledResolution(mc);
			font.drawCenteredString("GODMODE", res.getScaledWidth() / 2f, res.getScaledHeight() / 4f, 0xFF00FFAA);
		}
	}
	
	@Listener
	public void onPacketOut(PacketSendEvent event) {
		if (event.getPacket() instanceof CPacketConfirmTeleport) {
			if (this.auto.getValue()) {
				//if (!this.moving) event.setCanceled(true);
				if (!this.cancelPacket) {
					this.cancelPacket = true;
					this.render = true;
					if (!this.moving) event.setCanceled(true);
				}
				else {
					event.setCanceled(true);
				}
			}
			else event.setCanceled(true);
		}
	}
	
	@Listener
	public void onPacketIn(PacketReceiveEvent event) {
		if (!this.auto.getValue()
			|| !this.moving
			|| !this.cancelPacket) return;
		if (event.getPacket() instanceof SPacketPlayerPosLook) {
			final ISPacketPlayerPosLook packet = (ISPacketPlayerPosLook) event.getPacket();
			packet.setYaw(mc.player.rotationYaw);
			packet.setPitch(mc.player.rotationPitch);
		}
	}
	
	@Override
	public void onEnable() {
		this.cancelPacket = false;
		this.render = false;
	}
}
