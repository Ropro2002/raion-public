package me.robeart.raion.client.util.font;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.render.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class MinecraftFontRenderer extends BasicFontRenderer {
	
	private final FontData boldFont = new FontData();
	
	private final FontData italicFont = new FontData();
	
	private final FontData boldItalicFont = new FontData();
	
	private final int[] colorCode = new int[32];
	
	private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
	@Nullable
	HudModule hudModule = null;
	@NotNull
	public HudModule getHudModule() {
		if (hudModule == null) {
			hudModule = Raion.INSTANCE.getModuleManager().getModuleGeneric(HudModule.class);
		}
		return hudModule;
	}
	
	
	public MinecraftFontRenderer(Font font, boolean antialias) {
		for (int index = 0; index < 32; ++index) {
			int noClue = (index >> 3 & 1) * 85;
			int red = (index >> 2 & 1) * 170 + noClue;
			int green = (index >> 1 & 1) * 170 + noClue;
			int blue = (index & 1) * 170 + noClue;
			
			if (index == 6) {
				red += 85;
			}
			
			if (index >= 16) {
				red /= 4;
				green /= 4;
				blue /= 4;
			}
			
			this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
		}
		this.fontData.setFont(font, antialias);
		this.boldFont.setFont(font.deriveFont(Font.BOLD), antialias);
		this.italicFont.setFont(font.deriveFont(Font.ITALIC), antialias);
		this.boldItalicFont.setFont(font.deriveFont(Font.BOLD | Font.ITALIC), antialias);
	}
	
	@Deprecated
	@Override
	public float drawString(FontData fontData, String text, float x, float y, int color) {
		return 0;
	}
	
	public float drawStringRight(String text, float rightX, float y, int color) {
		float stringWidth = getStringWidth(text);
		return drawString(text, rightX - stringWidth, y, color);
	}
	
	public float drawStringRightClamped(String text, float rightX, float y, int color, float maxWidth) {
		float stringWidth = getStringWidth(text);
		return drawStringClamped(text, rightX - stringWidth, y, color, maxWidth);
	}
	
	/**
	 * Scales a drawn string so that it fits within the specified width
	 */
	public float drawStringClamped(String text, float x, float y, int color, float maxWidth) {
		float actualWidth = getStringWidth(text);
		float scale = maxWidth / actualWidth;
		float reverseScale = 1f;
		boolean shouldScale = scale < 1f;
		
		if (shouldScale) {
			reverseScale = 1/scale;
			GlStateManager.scale(scale, scale, 1f);
			x *= reverseScale;
			y *= reverseScale;
		}
		
		float out = drawString(text, x, y, color);
		
		if (shouldScale) {
			float reverse = 1/scale;
			GlStateManager.scale(reverse, reverse, 1f);
		}
		
		return out;
	}
	
	@Override
	public float drawString(String text, float x, float y, int color) {
		boolean shadow = getHudModule().shadow.getValue();
		int size = text.length();
		boolean rendermc = false;
		
		
		if (getHudModule().bitmap.getValue()) {
			if (shadow) {
				int shadowcolor = (color & 16579836) >> 2 | color & -16777216;
				return Math.max(fontData.getBitmapRenderer().drawString(text, x + 1, y + 1, shadowcolor, shadow), fontData.getBitmapRenderer().drawString(text, x, y, color, shadow));
			}
			return fontData.getBitmapRenderer().drawString(text, x, y, color, shadow);
		} else {
			for (int i = 0; i < size; i++) {
				char character = text.charAt(i);
				if (!getFontData().hasBounds(character)) {
					rendermc = true;
					break;
				}
			}
			if (rendermc) {
				x -= Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
				return Minecraft.getMinecraft().fontRenderer.drawString(text, x, y - 2, color, shadow);
			}
			if (shadow) {
				return drawStringWithShadow(text, x, y, color);
			}
			else {
				return drawString(text, x, y, color, false);
			}
		}
	}
	
	public float drawStringWithShadow(String text, float x, float y, int color) {
		return Math.max(drawString(text, x + 1, y + 1, color, true), drawString(text, x, y, color, false));
	}
	
	/**
	 * Renders text starting with the middle of the string at the given x, y positions. Includes a shadow effect as well.
	 */
	public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
		drawStringWithShadow(text, x - getStringWidth(text) / 2, y - getStringHeight(text) / 2, color);
	}
	
	/**
	 * Renders text starting with the middle of the string at the given x, y positions.
	 */
	public void drawCenteredString(String text, float x, float y, int color) {
		drawString(text, x - getStringWidth(text) / 2, y - getStringHeight(text) / 4, color);
	}
	
	public void drawCenteredString(String text, float x, float y, int color, boolean shadow) {
		drawString(text, x - getStringWidth(text) / 2, y - getStringHeight(text) / 4, color, shadow);
	}
	
	public float drawString(String text, float x, float y, int color, boolean shadow) {
		if (text == null)
			return 0;
		if (color == 553648127)
			color = 0xFFFFFF;
		
		if ((color & -67108864) == 0) {
			color |= -16777216;
		}
		
		// Shadow effect
		if (shadow) {
			color = (color & 16579836) >> 2 | color & -16777216;
		}
		
		if (getHudModule().bitmap.getValue()) {
			return fontData.getBitmapRenderer().drawString(text, x, y, color, shadow);
		}
		return drawStringNotBitmap(text, x, y, color, shadow);
	}
	
	/**
	 * Renders text using the color code rules within the default Minecraft font renderer.
	 */
	public float drawStringNotBitmap(String text, float x, float y, int color, boolean shadow) {
		// Current rendering information.
		FontData currentFont = fontData;
		float alpha = (color >> 24 & 0xff) / 255F;
		boolean randomCase = false, bold = false,
			italic = false, strikethrough = false,
			underline = false;
		
		// Multiplied positions since we'll be rendering this at half scale (to look nice!)
		x *= 4F;
		y *= 4F;
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.25F, 0.25F, 0.25);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, alpha);
		int size = text.length();
		currentFont.bind();
		for (int i = 0; i < size; i++) {
			char character = text.charAt(i);
			if (character == '\247' && i < size && i + 1 < size) {
				int colorIndex = colorcodeIdentifiers.indexOf(text.charAt(i + 1));
				if (colorIndex < 16) { // coloring
					bold = false;
					italic = false;
					randomCase = false;
					underline = false;
					strikethrough = false;
					currentFont = fontData;
					currentFont.bind();
					
					if (colorIndex < 0 || colorIndex > 15) {
						colorIndex = 15;
					}
					
					if (shadow) {
						colorIndex += 16;
					}
					
					int colorcode = colorCode[colorIndex];
					GlStateManager.color((float) (colorcode >> 16 & 255) / 255.0F, (float) (colorcode >> 8 & 255) / 255.0F, (float) (colorcode & 255) / 255.0F, alpha);
				}
				else if (colorIndex == 16) { // random case
					randomCase = true;
				}
				else if (colorIndex == 17) { // bold
					bold = true;
					if (italic) {
						currentFont = boldItalicFont;
						currentFont.bind();
					}
					else {
						currentFont = boldFont;
						currentFont.bind();
					}
				}
				else if (colorIndex == 18) { // strikethrough
					strikethrough = true;
				}
				else if (colorIndex == 19) { // underline
					underline = true;
				}
				else if (colorIndex == 20) { // italic
					italic = true;
					if (bold) {
						currentFont = boldItalicFont;
						currentFont.bind();
					}
					else {
						currentFont = italicFont;
						currentFont.bind();
					}
				}
				else if (colorIndex == 21) { // reset
					bold = false;
					italic = false;
					randomCase = false;
					underline = false;
					strikethrough = false;
					GlStateManager.color((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, alpha);
					currentFont = fontData;
					currentFont.bind();
				}
				i++;
			} else {
				x += drawCharacter(x, y, character, currentFont, randomCase, strikethrough, underline);
			}
		}
		GlStateManager.popMatrix();
		return x;
	}
	
	private float drawCharacter(float x, float y, char character, FontData currentFont, boolean randomCase, boolean strikethrough, boolean underline) {
		if (currentFont.hasBounds(character)) {
			if (randomCase) {
				char newChar = 0;
				while (currentFont.getCharacterBounds(newChar).width != currentFont.getCharacterBounds(character).width)
					newChar = (char) (Math.random() * 256);
				character = newChar;
			}
			FontData.CharacterData area = currentFont.getCharacterBounds(character);
			FontUtils.drawTextureRect(x, y, area.width, area.height,
				(float) area.x / currentFont.getTextureWidth(),
				(float) area.y / currentFont.getTextureHeight(),
				(float) (area.x + area.width) / currentFont.getTextureWidth(),
				(float) (area.y + area.height) / currentFont.getTextureHeight()
			);
			if (strikethrough)
				FontUtils.drawLine(x, y + area.height / 4f + 2, x + area.width / 2f, y + area.height / 4f + 2, 1F);
			if (underline)
				FontUtils.drawLine(x, y + area.height / 2f, x + area.width / 2f, y + area.height / 2f, 1F);
			return area.width + kerning;
		}
		return 0;
	}
	
	/**
	 * @return The height of the text which will be outputted by this font renderer. <br/>
	 * This information can normally be acquired through the {@link FontData} object, but with the MinecraftFontRenderer, multiple {@link FontData}s may be used.
	 */
	public float getStringHeight(String text) {
		if (text == null)
			return 0;
		
		if (getHudModule().bitmap.getValue()) {
			return fontData.getBitmapRenderer().getStringHeight(text);
		}
		
		float height = 0;
		FontData currentFont = fontData;
		boolean bold = false, italic = false;
		float size = text.length();
		
		for (int i = 0; i < size; i++) {
			char character = text.charAt(i);
			if (character == '\247' && i < size) {
				int colorIndex = colorcodeIdentifiers.indexOf(character);
				if (colorIndex < 16) { // coloring
					bold = false;
					italic = false;
				}
				else if (colorIndex == 17) { // bold
					bold = true;
					if (italic)
						currentFont = boldItalicFont;
					else
						currentFont = boldFont;
				}
				else if (colorIndex == 20) { // italic
					italic = true;
					if (bold)
						currentFont = boldItalicFont;
					else
						currentFont = italicFont;
				}
				else if (colorIndex == 21) { // reset
					bold = false;
					italic = false;
					currentFont = fontData;
				}
				i++;
			}
			else {
				if (currentFont.hasBounds(character)) {
					if (currentFont.getCharacterBounds(character).height > height)
						height = currentFont.getCharacterBounds(character).height;
				}
			}
		}
		return height / 4f;
	}
	
	/**
	 * @return The width of the text which will be outputted by this font renderer. <br/>
	 * This information can normally be acquired through the {@link FontData} object, but with the MinecraftFontRenderer, multiple {@link FontData}s may be used.
	 */
	
	public float getStringWidth(String text) {
		if (text == null || text.isEmpty())
			return 0;
		
		if (getHudModule().bitmap.getValue()) {
			return fontData.getBitmapRenderer().getStringWidth(text);
		}
		
		float width = 0;
		FontData currentFont = fontData;
		boolean bold = false, italic = false;
		int size = text.length();
		
		for (int i = 0; i < size; i++) {
			char character = text.charAt(i);
			if (character == '\247' && i < size) {
				int colorIndex = colorcodeIdentifiers.indexOf(character);
				if (colorIndex < 16) { // coloring
					bold = false;
					italic = false;
				}
				else if (colorIndex == 17) { // bold
					bold = true;
					if (italic)
						currentFont = boldItalicFont;
					else
						currentFont = boldFont;
				}
				else if (colorIndex == 20) { // italic
					italic = true;
					if (bold)
						currentFont = boldItalicFont;
					else
						currentFont = italicFont;
				}
				else if (colorIndex == 21) { // reset
					bold = false;
					italic = false;
					currentFont = fontData;
				}
				i++;
			}
			else {
				if (currentFont.hasBounds(character)) {
					width += currentFont.getCharacterBounds(character).width + kerning;
				}
			}
		}
		return width / 4f;
	}
	
	/**
	 * Applies a new font to the default font data as well as the bold, italic, and the bolditalic font data.
	 */
	public void setFont(Font font, boolean antialias) {
		this.fontData.setFont(font, antialias);
		this.boldFont.setFont(font.deriveFont(Font.BOLD), antialias);
		this.italicFont.setFont(font.deriveFont(Font.ITALIC), antialias);
		this.boldItalicFont.setFont(font.deriveFont(Font.BOLD | Font.ITALIC), antialias);
	}
}
