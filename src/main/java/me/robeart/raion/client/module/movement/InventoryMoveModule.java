package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.events.client.GetKeyStateEvent;
import me.robeart.raion.client.events.events.client.UnpressAllKeysEvent;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.module.player.FreecamModule;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.FloatValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class InventoryMoveModule extends Module {
	
	public FloatValue sensitivity = new FloatValue("Sensitivity", 7, 1, 20, 1);
	public BooleanValue chat = new BooleanValue("Chat", false);
	
	public InventoryMoveModule() {
		super("InventoryMove", "Allows you to move while a GUI is open", Category.MOVEMENT);
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
			Entity activeEntity = FreecamModule.INSTANCE.getActiveEntity();
			
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) activeEntity.rotationPitch -= sensitivity.getValue();
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) activeEntity.rotationPitch += sensitivity.getValue();
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) activeEntity.rotationYaw += sensitivity.getValue();
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) activeEntity.rotationYaw -= sensitivity.getValue();
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
				switch (mc.objectMouseOver.typeOfHit) {
					case ENTITY:
						mc.playerController.attackEntity(mc.player, mc.objectMouseOver.entityHit);
						break;
					case BLOCK:
						BlockPos blockpos = mc.objectMouseOver.getBlockPos();
						
						if (!mc.world.isAirBlock(blockpos)) {
							mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
							break;
						}
					case MISS:
				}
			}
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) activeEntity.setSprinting(true);
		}
	}
	
	@Listener
	private void onGetKeyState(GetKeyStateEvent event) {
		if (!matchesChatCondition()) return;
		
		// Sometimes the keycode is invalid (negative), idk why but that causes an exception so we need to handle that
		try {
			event.value = Keyboard.isKeyDown(event.keyBinding.getKeyCode());
		}
		catch (Exception ignored) {
		}
	}
	
	@Listener
	private void unpressAllKeys(UnpressAllKeysEvent event) {
		if (matchesChatCondition())
			event.shouldUnpress = false;
	}
	
	private boolean matchesChatCondition() {
		return (chat.getValue() || !(mc.currentScreen instanceof GuiChat));
	}
}
