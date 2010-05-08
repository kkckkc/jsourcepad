package kkckkc.syntaxpane.style;

import java.awt.Color;

public class StyleBean implements Style, TextStyle {
	private Color color;
	private Color background;
	boolean bold;
	boolean italic;
	boolean underline;

	public StyleBean(Color color) {
		this.color = color;
	}

	public StyleBean(Color color, Color background) {
		this.color = color;
		this.background = background;
	}
	
	public StyleBean(Color color, Color background, boolean bold,
			boolean italic, boolean underline) {
		this.color = color;
		this.background = background;
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
}