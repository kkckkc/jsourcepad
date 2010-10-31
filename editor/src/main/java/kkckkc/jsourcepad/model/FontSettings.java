package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.SettingsManager.Setting;
import kkckkc.utils.Os;

import java.awt.*;

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
		if (Os.isMac()) {
			return new FontSettings("Monaco", Font.PLAIN, 12);
		} else if (Os.isLinux()) {
			return new FontSettings("Liberation Mono", Font.PLAIN, 12);
		} else {
            return new FontSettings("Courier New", Font.PLAIN, 12);
        }
	}

	public Font asFont() {
	    return new Font(font, style, size);
    }

}
