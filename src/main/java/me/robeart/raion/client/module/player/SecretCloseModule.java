package me.robeart.raion.client.module.player;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.minecraft.GLUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.FloatValue;
import me.robeart.raion.client.value.IntValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.tileentity.TileEntityHopper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Arrays;

public class SecretCloseModule extends Module {
	
	public BooleanValue visuals = new BooleanValue("Render", true);
	public IntValue drawmode = new IntValue("Mode", 1, 1, 14, 1, visuals);
	public FloatValue width = new FloatValue("Width", 2, 0.1f, 10, 0.1f, visuals);
	public IntValue alpha = new IntValue("Alpha", 80, 0, 100, 1, visuals);
	public ListValue mode = new ListValue("Mode", "Hopper", Arrays.asList("Hopper", "Everything"));
	public IntValue distance = new IntValue("Distance", 50, 7, 200, 1);
	
	
	public SecretCloseModule() {
		super("SecretClose", "Closes a GUI without letting the server know", Category.PLAYER);
	}
	
	@Listener
	private void onPacketSend(PacketSendEvent event) {
		if (event.getPacket() instanceof CPacketCloseWindow && event.getStage() == EventStageable.EventStage.PRE) {
			if (mode.getValue().equalsIgnoreCase("hopper")) {
				if (mc.player.openContainer instanceof ContainerHopper) {
					event.setCanceled(true);
				}
			}
			else {
				event.setCanceled(true);
			}
		}
	}
	
	@Listener
	private void onRender3D(Render3DEvent event) {
		if (visuals.getValue()) {
			for (Object o : Minecraft.getMinecraft().world.loadedTileEntityList) {
				if (o instanceof TileEntityHopper) {
					if (mc.player.getDistanceSqToCenter(((TileEntityHopper) o).getPos()) < (distance.getValue() * distance
						.getValue())) {
						GLUtils.circleESP(((TileEntityHopper) o).getPos()
							.add(0, 0, 0), 7, GLUtils.getColor(alpha.getValue()), drawmode.getValue(), width.getValue());
					}
				}
			}
		}
	}
	
	@Override
	public String getHudInfo() {
		return mode.getValue();
	}
}
