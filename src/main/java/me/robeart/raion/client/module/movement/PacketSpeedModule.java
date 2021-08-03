package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.IntValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Arrays;

/**
 * @author Robeart
 */
public class PacketSpeedModule extends Module {
	
	public ListValue yMode = new ListValue("Mode", "Y+", Arrays.asList("Y+", "Y-"));
	public IntValue yDistance = new IntValue("Distance", 1000, 256, 10000, 100);
	public IntValue distance = new IntValue("Distance", 20, 1, 200, 1);
	public int teleportId;
	public double x, y, z;
	
	public PacketSpeedModule() {
		super("PacketSpeed", "Go fast", Category.MOVEMENT);
	}
	
	@Listener
	public void event(UpdateWalkingPlayerEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (mc.player.isDead) return;
		if (this.teleportId <= 0) {
			CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (this.yMode
				.getValue()
				.equalsIgnoreCase("Y+") ? +this.yDistance.getValue() : -this.yDistance.getValue()), mc.player.posZ, mc.player.onGround);
			mc.player.connection.sendPacket(bounds);
			return;
		}
		mc.player.setVelocity(0, 0, 0);
		double[] directionalSpeed = directionSpeed(0.06);
		if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindForward
			.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft
			.isKeyDown()) {
			if (directionalSpeed[0] != 0.0 || directionalSpeed[1] != 0.0) {
				for (int i = 0; i <= 2; ++i) {
					mc.player.setVelocity(directionalSpeed[0] * i, 0, directionalSpeed[1] * i);
					this.move(directionalSpeed[0] * i, 0, directionalSpeed[1] * i);
				}
			}
		}
		event.setCanceled(true);
	}
	
	public double[] directionSpeed(double speed) {
		float forward = mc.player.movementInput.moveForward;
		float side = mc.player.movementInput.moveStrafe;
		float yaw = mc.player.rotationYaw;
		if (forward != 0.0f) {
			if (side > 0.0f) {
				yaw += ((forward > 0.0f) ? -45 : 45);
			}
			else if (side < 0.0f) {
				yaw += ((forward > 0.0f) ? 45 : -45);
			}
			side = 0.0f;
			if (forward > 0.0f) {
				forward = 1.0f;
			}
			else if (forward < 0.0f) {
				forward = -1.0f;
			}
		}
		double posX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + side * speed * Math.sin(Math.toRadians(yaw + 90.0f));
		double posZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - side * speed * Math.cos(Math.toRadians(yaw + 90.0f));
		return new double[]{posX, posZ};
	}
	
	@Listener
	public void eventPacketSent(PacketSendEvent event) {
		//if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) event.setCanceled(true);
	}
	
	@Listener
	public void eventPacketReceived(PacketReceiveEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (event.getPacket() instanceof SPacketPlayerPosLook) {
			SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
			if (mc.player != null && mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
				this.teleportId = packet.getTeleportId();
			}
		}
	}
	
	@Override
	public void onEnable() {
		Vec3d dir = mc.player.getLookVec().normalize();
		x = mc.player.posX + (dir.x * distance.getValue());
		y = mc.player.posY;
		z = mc.player.posZ + (dir.z * distance.getValue());
		this.teleportId = 0;
		CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (this.yMode.getValue()
			.equalsIgnoreCase("Y+") ? +this.yDistance.getValue() : -this.yDistance.getValue()), mc.player.posZ, mc.player.onGround);
		mc.player.connection.sendPacket(bounds);
	}
	
	public void move(double x, double y, double z) {
		CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
		mc.player.connection.sendPacket(pos);
		CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + (this.yMode
			.getValue()
			.equalsIgnoreCase("Y+") ? +this.yDistance.getValue() : -this.yDistance.getValue()), mc.player.posZ + z, mc.player.onGround);
		mc.player.connection.sendPacket(bounds);
		this.teleportId++;
		//mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
		mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
		mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
	}
	
}
