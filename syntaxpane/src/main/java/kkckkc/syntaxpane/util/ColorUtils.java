package kkckkc.syntaxpane.util;

import java.awt.Color;

public class ColorUtils {
	public static Color offset(Color base) {
		return offset(base, 1);
	}

	private static final double FACTOR = 0.98;

	private static Color darker(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();

		return new Color(
				Math.max(adjust(r, FACTOR), 0), 
				Math.max(adjust(g, FACTOR), 0), 
				Math.max(adjust(b, FACTOR), 0));
	}

	private static Color brighter(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();

		int i = (int) (1.0 / (1.0 - FACTOR));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i);
		}
		if (r > 0 && r < i)
			r = i;
		if (g > 0 && g < i)
			g = i;
		if (b > 0 && b < i)
			b = i;

		return new Color(
				Math.min(adjust(r, 1 / FACTOR), 255), 
				Math.min(adjust(g, 1 / FACTOR), 255), 
				Math.min(adjust(b, 1 / FACTOR), 255));
	}

	private static final int adjust(int color, double factor, double gamma) {
		double c = ((double) color) / 255;
		double gammaCorrectedC = Math.pow(c, gamma);
		double gammaCorrectedAdjusted = gammaCorrectedC * factor;
		double adjusted = Math.pow(gammaCorrectedAdjusted, 1 / gamma);
		return (int) (adjusted * 255);
	}
		
	private static final int adjust(int color, double factor) {
		return adjust(color, factor, 1 / 2.2);
	}
	
	public static Color offset(Color base, int amount) {
		int red = base.getRed();
		int green = base.getGreen();
		int blue = base.getBlue();
		int grayScaleIntensity = (int) (Math.sqrt(.241 * red * red + .691 * green * green + .068 * blue * blue) + .5);

		if (grayScaleIntensity < 128) {
			for (int i = 0; i < amount; i++)
				base = brighter(base);
		} else {
			for (int i = 0; i < amount; i++)
				base = darker(base);
		}

		return base;
	}
}
