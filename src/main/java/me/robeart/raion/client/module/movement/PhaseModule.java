package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.events.player.*;
import me.robeart.raion.client.events.events.render.SetOpaqueCubeEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.DoubleValue;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class PhaseModule extends Module {
	
	public DoubleValue speed = new DoubleValue("Speed", 10, 0, 50, 0.1);
	
	public PhaseModule() {
		super("Phase", "For the dupe", Category.MOVEMENT);
	}
	
	@Override
	public void onEnable() {
		if (mc.player != null && mc.world != null) {
			mc.player.capabilities.isFlying = true;
			mc.player.noClip = true;
		}
	}
	
	@Override
	public void onDisable() {
		if (mc.player != null && mc.world != null) {
			mc.player.capabilities.isFlying = false;
			mc.player.noClip = false;
		}
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		mc.player.motionY = 0.0;
		mc.player.noClip = true;
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY += 0.0123456789;
		}
		if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY -= 0.0123456789;
		}
	}
	
	@Listener
	private void onMove(MoveEvent event) {
		mc.player.noClip = true;
	}
	
	@Listener
	private void pushOutofBlock(PushOutOfBlocksEvent event) {
		event.setCanceled(true);
	}
	
	@Listener
	private void setOpaqueCube(SetOpaqueCubeEvent event) {
		event.setCanceled(true);
	}
	
	@Listener
	private void applyCollision(ApplyCollisionEvent event) {
		event.setCanceled(true);
	}
	
	@Listener
	public void pushedByWater(PushedByWaterEvent event) {
		event.setCanceled(true);
	}
	
}
