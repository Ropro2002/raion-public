package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.entity.EntityLivingBaseTravelEvent;
import me.robeart.raion.client.events.events.entity.ShouldStopElytraEvent;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent;
import me.robeart.raion.client.imixin.IEntity;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.MathUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.DoubleValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.lang.reflect.Field;
import java.util.Arrays;

import static net.minecraft.network.play.client.CPacketEntityAction.Action.START_FALL_FLYING;

/**
 * @author cats
 * Credit to seppuku for some of the flight code
 */

public class ElytraFlightModule extends Module {
	public ListValue mode = new ListValue("Mode", "Control", Arrays.asList("Control", "Packet"));
	private BooleanValue smoothTakeoff = new BooleanValue("Smooth Take Off", true);
	private BooleanValue smoothLand = new BooleanValue("Smooth Landing", true);
	private BooleanValue spamTakeoff = new BooleanValue("Spam Take Off", false);
	private DoubleValue hSpeed = new DoubleValue("Horizontal", 2.0, 0, 5, 0.1D);
	private DoubleValue vSpeed = new DoubleValue("Vertical", 1.0, 0, 5, 0.1D);
	private DoubleValue descent = new DoubleValue("Descend", 0.0, 0.0, 0.5, 0.0000000001);
	private BooleanValue noKick = new BooleanValue("No Kick", true);
	private BooleanValue allowUp = new BooleanValue("Allow Up", false);
	private BooleanValue spoofPitch = new BooleanValue("Spoof Pitch", true);

	private Field pitch;

	public ElytraFlightModule() {
		super("ElytraFly", "Allows you to fly at fast speeds using an elytra", Category.MOVEMENT);

		pitch = Arrays.stream(CPacketPlayer.class.getDeclaredFields())
			.filter(f -> f.getName().equals("pitch") || f.getName().equals("field_149473_f"))
			.findAny()
			.orElseThrow(RuntimeException::new);
	}

	@Override
	public String getHudInfo() {
		return "[" + mode.getValue() + "]";
	}

	@Listener
	public void onSmoothLand(ShouldStopElytraEvent event) {
		if (smoothLand.getValue()) {
			event.setWorldRemote(false);
		}
	}

	@Override
	public void onDisable() {
		if (mc.player == null) return;
		mc.player.capabilities.isFlying = false;
	}

	@Listener
	public void onSend(PacketSendEvent event) {
		if (mc.player == null) return;
		if (event.getPacket() instanceof CPacketPlayerAbilities) {
			CPacketPlayerAbilities packet = (CPacketPlayerAbilities) event.getPacket();
			packet.setFlying(false);
			packet.setAllowFlying(false);
			packet.setFlySpeed(0.05F);
		}
		if (spoofPitch.getValue() && (!mc.gameSettings.keyBindSprint.isKeyDown() || !mode.getValue()
			.equals("Control")) && !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.isElytraFlying() && event.getPacket() instanceof CPacketPlayer) {
			CPacketPlayer packet = (CPacketPlayer) event.getPacket();
			try {
				pitch.set(packet, 0);
			}
			catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	@Listener
	public void onWalkingUpdate(UpdateWalkingPlayerEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;

		if (event.getStage() == EventStageable.EventStage.PRE) {
			ItemStack itemstack = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			boolean equipped = itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack);
			if (mode.getValue().equalsIgnoreCase("Control")) {
				if (!mc.player.isElytraFlying()) {
					if (
						smoothTakeoff.getValue()
							&&
							mc.gameSettings.keyBindJump.isKeyDown()
							&&
							!mc.player.onGround
							&&
							mc.player.motionY < 0.0D
							&&
							!mc.player.isInWater()
					) {

						if (equipped) {
							mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, START_FALL_FLYING));
							((IEntity) mc.player).setFlag0(7, true);
						}
					}

					return;
				}
				else {
					if (spamTakeoff.getValue()) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, START_FALL_FLYING));
						((IEntity) mc.player).setFlag0(7, true);
					}
				}

				if (mc.gameSettings.keyBindSprint.isKeyDown()) return;

				//mc.player.motionY = 0;

				mc.player.setVelocity(0, 0, 0);

				mc.player.jumpMovementFactor = this.hSpeed.getValue().floatValue();

				final double[] dir = MathUtils.directionSpeed(this.hSpeed.getValue());

				if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
					mc.player.motionX = dir[0];
					mc.player.motionZ = dir[1];
				}
				else {
					mc.player.motionX = 0;
					mc.player.motionZ = 0;
				}


				if (allowUp.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.player.motionY += this.vSpeed.getValue() / 5;
				}

				if (mc.gameSettings.keyBindSneak.isKeyDown()) {
					mc.player.motionY -= this.vSpeed.getValue() / 5;
				}
			}
			else if (mode.getValue().equalsIgnoreCase("Packet")) {
				if (equipped) {
					mc.player.capabilities.isFlying = true;

					float hSpeedV = hSpeed.getValue().floatValue() / 10;
					float vSpeedV = vSpeed.getValue().floatValue() / 10;

					float flySpeed = mc.player.capabilities.getFlySpeed();
					if (mc.player.movementInput.moveForward == 0 && mc.player.movementInput.moveStrafe == 0) {
						flySpeed = vSpeedV;
					}
					else {
						flySpeed += 0.01;
					}
					if (flySpeed > hSpeedV) {
						flySpeed = hSpeedV;
					}
					mc.player.capabilities.setFlySpeed(flySpeed);

					//mc.player.capabilities.setFlySpeed(hSpeed.getValue().floatValue());
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, START_FALL_FLYING));
				}
				else {
					mc.player.capabilities.isFlying = false;
				}
			}
		}
		else if (event.getStage() == EventStageable.EventStage.POST) {
			if (mode.getValue().equalsIgnoreCase("Control")) {
				/*if (spamTakeoff.getValue()) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, START_SNEAKING));
					((IEntity) mc.player).setFlag0(7, false);
				}*/
			}
		}
	}

	@Listener
	public void elytraMoveDown(EntityLivingBaseTravelEvent event) {
		if (mc.gameSettings.keyBindSprint.isKeyDown()) return;
		if (event.getEntity() == mc.player) {
			if (mode.getValue().equalsIgnoreCase("Control")) {
				if (!event.getEntity()
					.isInWater() || event.getEntity() != null && ((EntityPlayerSP) event.getEntity()).capabilities.isFlying) {
					if (!event.getEntity().isInLava()) {
						if (event.getEntity().isElytraFlying()) {
							event.setCanceled(true);

							//We replicate the code that minecraft does at this event, but with a custom y movement

							Vec3d vec3d = event.getEntity().getLookVec();
							float f = event.getEntity().rotationPitch * 0.017453292F;
							double d6 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
							double d8 = Math.sqrt(event.getEntity().motionX * event.getEntity().motionX + event.getEntity().motionZ * event
								.getEntity().motionZ);
							vec3d.length();


							if (f < 0.0F) {
								double d10 = d8 * (double) (-MathHelper.sin(f)) * 0.04D;
								event.getEntity().motionY += d10 * 3.2D;
								event.getEntity().motionX -= vec3d.x * d10 / d6;
								event.getEntity().motionZ -= vec3d.z * d10 / d6;
							}

							if (d6 > 0.0D) {
								event.getEntity().motionX += (vec3d.x / d6 * d8 - event.getEntity().motionX) * 0.1D;
								event.getEntity().motionZ += (vec3d.z / d6 * d8 - event.getEntity().motionZ) * 0.1D;
							}

							event.getEntity().motionX *= 0.9900000095367432D;
							event.getEntity().motionZ *= 0.9900000095367432D;
							if (mc.gameSettings.keyBindSneak.isKeyDown() || (this.allowUp.getValue() && mc.gameSettings.keyBindJump
								.isKeyDown())) {
								event.getEntity()
									.move(MoverType.SELF, event.getEntity().motionX, event.getEntity().motionY, event.getEntity().motionZ);
							}
							else {
								if (this.noKick.getValue()) {
									event.getEntity()
										.move(MoverType.SELF, event.getEntity().motionX, -this.descent.getValue(), event
											.getEntity().motionZ);
								}
								else {
									event.getEntity()
										.move(MoverType.SELF, event.getEntity().motionX, 0, event.getEntity().motionZ);

								}
							}
						}
					}
				}
			}
		}
	}

	public boolean fakeElytraFly() {
		return !this.getState() || !this.mode.getValue().equalsIgnoreCase("Packet");
	}
}
