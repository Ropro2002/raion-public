package me.robeart.raion.client.gui.discordrpc;

import me.robeart.raion.client.module.misc.DiscordRPCModule;
import me.robeart.raion.client.util.data.Vec2i;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author cookiedragon234 11/Nov/2019
 */
public class DiscordPrecenceChangerGui extends GuiScreen {
	private final DiscordRPCModule.RpcSettings rpcSettings;
	private GuiTextField[] textFields = new GuiTextField[6];
	// mm == main menu, sp == singleplayer, mp == multiplayer
	private GuiTextField mmStateField;
	private GuiTextField mmDescField;
	private GuiTextField spStateField;
	private GuiTextField spDescField;
	private GuiTextField mpStateField;
	private GuiTextField mpDescField;
	
	public DiscordPrecenceChangerGui(DiscordRPCModule.RpcSettings rpcSettings) {
		this.rpcSettings = rpcSettings;
	}
	
	@SuppressWarnings("PointlessArithmeticExpression")
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		// 3 columns of 2 rows
		
		final int BUTTON_PADDING = 10; // 10px
		final int WIDTH_PADDING = 200; // 200px
		final int HEIGHT_PADDING = 100; // 100px
		
		int paddedWidth = width - WIDTH_PADDING;
		int paddedHeight = height - HEIGHT_PADDING;
		
		int txtWidth = (paddedWidth / 3) - (BUTTON_PADDING * 2);
		int txtHeight = 20;
		
		int firstColumnStart = (paddedWidth / 3) * 0;
		int secondColumnStart = (paddedWidth / 3) * 1;
		int thirdColumnStart = (paddedWidth / 3) * 2;
		
		int firstRowStart = (paddedHeight / 2) * 0;
		int secondRowStart = (paddedHeight / 2) * 1;
		
		Vec2i row1col1 = new Vec2i(firstColumnStart + WIDTH_PADDING, firstRowStart + HEIGHT_PADDING);
		Vec2i row1col2 = new Vec2i(secondColumnStart + WIDTH_PADDING, firstRowStart + HEIGHT_PADDING);
		Vec2i row1col3 = new Vec2i(thirdColumnStart + WIDTH_PADDING, firstRowStart + HEIGHT_PADDING);
		
		Vec2i row2col1 = new Vec2i(firstColumnStart + WIDTH_PADDING, secondRowStart + HEIGHT_PADDING);
		Vec2i row2col2 = new Vec2i(secondColumnStart + WIDTH_PADDING, secondRowStart + HEIGHT_PADDING);
		Vec2i row2col3 = new Vec2i(thirdColumnStart + WIDTH_PADDING, secondRowStart + HEIGHT_PADDING);
		
		mmStateField = new GuiTextField(0, fontRenderer, row1col1.x, row1col1.y, 202, 20);
		mmDescField = new GuiTextField(0, fontRenderer, row2col1.x, row2col1.y, 202, 20);
		
		spStateField = new GuiTextField(0, fontRenderer, row1col2.x, row1col2.y, 202, 20);
		spDescField = new GuiTextField(0, fontRenderer, row2col2.x, row2col2.y, 202, 20);
		
		mpStateField = new GuiTextField(0, fontRenderer, row1col3.x, row1col3.y, 202, 20);
		mpDescField = new GuiTextField(0, fontRenderer, row2col3.x, row2col3.y, 202, 20);
		
		mmStateField.setText(rpcSettings.mmState);
		mmDescField.setText(rpcSettings.mmDesc);
		spStateField.setText(rpcSettings.spState);
		spDescField.setText(rpcSettings.spDesc);
		mpStateField.setText(rpcSettings.mpState);
		mpDescField.setText(rpcSettings.mpDesc);
		
		textFields[0] = mmStateField;
		textFields[1] = mmDescField;
		textFields[2] = spStateField;
		textFields[3] = spDescField;
		textFields[4] = mpStateField;
		textFields[5] = mpDescField;
		
		super.initGui();
	}
	
	@Override
	public void updateScreen() {
		for (GuiTextField textField : textFields) {
			textField.updateCursorCounter();
		}
		super.updateScreen();
	}
	
	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		for (GuiTextField textField : textFields) {
			textField.mouseClicked(x, y, button);
		}
		super.mouseClicked(x, y, button);
	}
	
	@Override
	protected void keyTyped(char character, int keyCode) throws IOException {
		for (int i = 0; i < textFields.length; i++) {
			GuiTextField textField = textFields[i];
			
			// Allow using tab to switch between input fields
			if (textField.isFocused() && character == '\t' && i + 1 < textFields.length) {
				textField.setFocused(false);
				textFields[i + 1].setFocused(true);
				return;
			}
			
			// Allow using enter key to save
			// TODO: Press save button
			/*if(character == '\r')
			{
				actionPerformed(buttonList.get(0));
				return;
			}*/
			
			textField.textboxKeyTyped(character, keyCode);
		}
		super.keyTyped(character, keyCode);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		
		for (GuiTextField textField : textFields) {
			textField.drawTextBox();
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onGuiClosed() {
		rpcSettings.mmState = mmStateField.getText();
		rpcSettings.mmDesc = mmDescField.getText();
		rpcSettings.spState = spStateField.getText();
		rpcSettings.spDesc = spDescField.getText();
		rpcSettings.mpState = mpStateField.getText();
		rpcSettings.mpDesc = mpDescField.getText();
		
		Keyboard.enableRepeatEvents(false);
	}
}
