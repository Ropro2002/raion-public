package me.robeart.raion.client.events;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.Render2DEvent;
import me.robeart.raion.client.gui.RaionMainMenu;
import me.robeart.raion.client.gui.cui.CuiManagerGui;
import me.robeart.raion.client.macro.Macro;
import me.robeart.raion.client.managers.MacroManager;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ForgeEventProcessor {
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onForgeEvent(Event event) {
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onGui(GuiOpenEvent event) {
		if (event.getGui() != null && event.getGui().getClass() == GuiMainMenu.class) {
			event.setGui(new RaionMainMenu());
		}
	}
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;
		Raion.INSTANCE.getEventManager()
			.dispatchEvent(new Render2DEvent(event.getPartialTicks(), new ScaledResolution(Minecraft.getMinecraft())));
		Raion.INSTANCE.getPopupManager().onRender();
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		try {
			if (Keyboard.getEventKeyState()) {
				int eventKey = Keyboard.getEventKey();
				String keyName = Keyboard.getKeyName(eventKey);
				
				if (eventKey == Keyboard.KEY_NONE)
					return;
				
				if (eventKey == Keyboard.KEY_LBRACKET) {
					Minecraft.getMinecraft().displayGuiScreen(CuiManagerGui.INSTANCE);
					return;
				}
				
				try {
					for (Module module : Raion.INSTANCE.getModuleManager().getModuleList()) {
						if (keyName.equalsIgnoreCase(module.getBind())) {
							module.toggle();
						}
					}
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
				
				try {
					Key key = Key.Companion.fromCode(eventKey);
					Macro macro = MacroManager.INSTANCE.get(key);
					if (macro != null) {
						macro.run();
					}
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}
