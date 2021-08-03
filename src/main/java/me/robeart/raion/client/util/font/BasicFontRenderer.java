package me.robeart.raion.client.util.font;

import net.minecraft.client.renderer.GlStateManager;

public class BasicFontRenderer implements FontRenderer {
	
	protected final FontData fontData = new FontData();
	protected int kerning = 0;
	
	public BasicFontRenderer() {
	}
	
	@Override
	public float drawString(FontData fontData, String text, float x, float y, int color) {
		if (!fontData.hasFont())
			return 0;
		GlStateManager.enableBlend();
		fontData.bind();
		GLManager.glColor(color);
		int size = text.length();
		for (int i = 0; i < size; i++) {
			char character = text.charAt(i);
			if (fontData.hasBounds(character)) {
				FontData.CharacterData area = fontData.getCharacterBounds(character);
				FontUtils.drawTextureRect(x, y, area.width, area.height,
					(float) area.x / fontData.getTextureWidth(),
					(float) area.y / fontData.getTextureHeight(),
					(float) (area.x + area.width) / fontData.getTextureWidth(),
					(float) (area.y + area.height) / fontData.getTextureHeight()
				);
				x += area.width + kerning;
			}
		}
		return x;
	}
	
	@Override
	public float drawString(String text, float x, float y, int color) {
		return drawString(fontData, text, x, y, color);
	}
	
	public int getKerning() {
		return kerning;
	}
	
	public void setKerning(int kerning) {
		this.kerning = kerning;
	}
	
	@Override
	public FontData getFontData() {
		return fontData;
	}
}
