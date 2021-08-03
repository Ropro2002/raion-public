package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.events.events.player.MoveEvent;
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.DoubleValue;
import me.robeart.raion.client.value.IntValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketFlyModule extends Module {

	public IntValue yDistance = new IntValue("Distance", 1000, 256, 10000, 100);
	public ListValue mode = new ListValue("Mode", "Factor", Arrays.asList("Factor", "Setback", "Fast"));
	public ListValue phase = new ListValue("Phase", "Full", Arrays.asList("Full", "Off", "Semi"));
	public DoubleValue factor = new DoubleValue("Speed", 1.0, 0, 10.0, 0.1);
	private BooleanValue antikick = new BooleanValue("Anti Kick", true);
	private int teleportId;
	//private BooleanValue phase = new BooleanValue("Phase", true);
	private int otherids;
	private Map<Integer, PacketData> packetDataMap = new ConcurrentHashMap<>();
	private ArrayList<CPacketPlayer> packets = new ArrayList<CPacketPlayer>();
	public PacketFlyModule() {
		super("PacketFly", "Allows you to fly using packets", Category.MOVEMENT);
	}

	@Listener
	public void event(UpdateWalkingPlayerEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		mc.player.motionX = mc.player.motionY = mc.player.motionZ = 0.0f;
		if (!mode.getValue().equalsIgnoreCase("Fast") && this.teleportId == 0) {
			move(0.0, 0.0, 0.0, false);
			return;
		}
		boolean collide = collideCheck();
		double n2 = 0;
		if (mc.gameSettings.keyBindJump.isKeyDown() && (collide /*|| ZG.b()*/))
			n2 = antikick.getValue() && !collide ? (idCheck(mode.getValue()
				.equalsIgnoreCase("Fast") ? 10 : 20) ? 1.748524532E-314 : 1.6636447E-314) : 1.6636447E-314;
		else if (mc.gameSettings.keyBindSneak.isKeyDown()) n2 = 1.6636447E-314;
		else n2 = collide ? 0.0 : idCheck(4) ? antikick.getValue() ? 5.941588215E-315 : 0.0 : 0.0;
		if (phase.getValue().equalsIgnoreCase("Off") && collide && n2 != 0.0) n2 = 0.0;
		double[] speed = directionSpeed((phase.getValue()
			.equalsIgnoreCase("Off") && collide) ? 1.6636447E-314 : 8.48798317E-316);
		int i = 0;
		while (i <= (mode.getValue().equalsIgnoreCase("Factor") ? factor.getValue() : 1)) {
			move(mc.player.motionX = speed[0], mc.player.motionY = n2, mc.player.motionZ = speed[1], !(mode.getValue()
				.equalsIgnoreCase("Fact0r")));
			i++;
		}
	}

	@Listener
	public void onMove(MoveEvent event) {
		event.setX(mc.player.motionX);
		event.setY(mc.player.motionY);
		event.setZ(mc.player.motionZ);
		if (!(phase.getValue().equalsIgnoreCase("Full")) && ((phase.getValue()
			.equalsIgnoreCase("Semi")) || collideCheck()) && this.teleportId != 0)
			mc.player.noClip = true;
	}

	@Listener
	public void eventPacketSent(PacketSendEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
        /*if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
            event.setCanceled(true);
        }*/
		if (event.getPacket() instanceof CPacketPlayer) {
			CPacketPlayer packet = (CPacketPlayer) event.getPacket();
			if (this.packets.contains(packet)) {
				this.packets.remove(packet);
				return;
			}
			event.setCanceled(true);
		}
	}

	@Listener
	public void eventPacketReceived(PacketReceiveEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (event.getPacket() instanceof SPacketPlayerPosLook) {
			SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
			if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
				PacketData data = packetDataMap.get(packet.getTeleportId());
				if (!mode.getValue()
					.equalsIgnoreCase("Fast") && data != null && data.getX() == packet.getX() && data.getY() == packet.getY() && data
					.getZ() == packet.getZ()) {
					event.setCanceled(true);
					return;
				}
				this.teleportId = packet.getTeleportId();
			}
		}
	}

    /*@Listener
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
    }*/

	@Override
	public void onDisable() {
		this.teleportId = 0;
		this.otherids = 0;
		this.packets.clear();
		this.packetDataMap.clear();
	}

	private boolean collideCheck() {
		return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(0.0, 0.0, 0.0)).isEmpty();
	}

	private boolean idCheck(int id) {
		if (++this.otherids >= id) {
			this.otherids = 0;
			return true;
		}
		return false;
	}

	public void move(double x, double y, double z, boolean teleportPacket) {
		CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
		this.packets.add(pos);
		mc.player.connection.sendPacket(pos);
		CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y - 1337.420, mc.player.posZ + z, mc.player.onGround);
		this.packets.add(bounds);
		mc.player.connection.sendPacket(bounds);
		if (teleportPacket) {
			this.teleportId++;
			mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
			this.packetDataMap.put(this.teleportId, new PacketData(mc.player.posX + x, mc.player.posY + y, mc.player.posZ, System
				.currentTimeMillis()));
		}
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

	public static class PacketData {
		private double x;
		private double y;
		private double z;
		private long time;

		public PacketData(double x, double y, double z, long time) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.time = time;
		}

		public double getX() {
			return this.x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return this.y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getZ() {
			return this.z;
		}

		public void setZ(double z) {
			this.z = z;
		}

		public long getTime() {
			return this.time;
		}

		public void setTime(long time) {
			this.time = time;
		}
	}

}
