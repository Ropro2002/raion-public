package me.robeart.raion.client.util.font;

public interface FontRenderer {
	
	/**
	 * @return The width (in pixels) of the text rendered.
	 */
	@Deprecated
	float drawString(FontData fontData, String text, float x, float y, int color);
	
	@Deprecated
	default int drawString(FontData fontData, String text, int x, int y, int color) {
		return (int) drawString(fontData, text, (float) x, (float) y, color);
	}
	
	/**
	 * @return The width (in pixels) of the text rendered.
	 */
	float drawString(String text, float x, float y, int color);
	
	default int drawString(String text, int x, int y, int color) {
		return (int) drawString(text, (float) x, (float) y, color);
	}
	
	/**
	 * @return The {@link FontData} used by this FontRenderer.
	 */
	FontData getFontData();
	
}
