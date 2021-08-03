package me.robeart.raion.mixin.common.gui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.client.ClickMouseButtonChatEvent;
import me.robeart.raion.client.events.events.render.DrawScreenGuiChatEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public class MixinGuiChat {
	
	@Shadow
	protected GuiTextField inputField;
	
	@Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
	public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		DrawScreenGuiChatEvent event = new DrawScreenGuiChatEvent(mouseX, mouseY, partialTicks, this.inputField);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
        /*if(Raion.INSTANCE.getModuleManager().isEnabled("Hud")) {
            GLUtils.drawRect(2, this.height - 14, 1, 12, GLUtils.getColor(100));
            int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(inputField.getText()) + 10 > GLUtils.getScreenWidth() / 3 ? Minecraft.getMinecraft().fontRenderer.getStringWidth(inputField.getText()) + 10 : GLUtils.getScreenWidth() / 4;
            GLUtils.drawRect(3, this.height - 14, width, 12, Integer.MIN_VALUE);
            if(inputField.getText().startsWith(Raion.INSTANCE.getCommandManager().getPrefix())) {
                GLUtils.glColor(0xFFFF0000);
                GLUtils.drawBorder(2,2, this.height - 14, width + 2, this.height - 2);
            }
            this.inputField.drawTextBox();
            ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
            {
                this.handleComponentHover(itextcomponent, mouseX, mouseY);
            }
            super.drawScreen(mouseX, mouseY, partialTicks);
            callbackInfo.cancel();
        } else {
            if(inputField.getText().startsWith(Raion.INSTANCE.getCommandManager().getPrefix())) {
                GLUtils.glColor(0xFFFF0000);
                GLUtils.drawBorder(2,2, this.height - 14, this.width - 2, this.height - 2);
            }
        }*/
	}
	
	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	public void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
		ClickMouseButtonChatEvent event = new ClickMouseButtonChatEvent(mouseX, mouseY, mouseButton);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	
}
