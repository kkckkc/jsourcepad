package kkckkc.jsourcepad.model;

import java.awt.Font;

import kkckkc.jsourcepad.model.SettingsManager.Setting;
import kkckkc.syntaxpane.util.EnvironmentUtils;

public class FontSettings implements Setting {

	private String font;
	private int style;
	private int size;

	public FontSettings() {}

	public FontSettings(String font, int style, int size) {
		this.font = font;
		this.style = style;
		this.size = size;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public FontSettings getDefault() {
		if (EnvironmentUtils.isMac()) {
			return new FontSettings("Monaco", Font.PLAIN, 12);
		} else {
			return new FontSettings("Liberation Mono", Font.PLAIN, 12);
		}
	}

	public Font asFont() {
	    return new Font(font, style, size);
    }

}
