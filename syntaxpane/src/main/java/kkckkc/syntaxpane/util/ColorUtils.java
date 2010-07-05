package kkckkc.syntaxpane.util;

import java.awt.Color;

public class ColorUtils {
	public static Color offset(Color base) {
		return offset(base, 1);
	}

	private static final double FACTOR = 0.95;

	private static Color darker(Color c) {
		return new Color(
				Math.max((int) (c.getRed() * FACTOR), 0), 
				Math.max((int) (c.getGreen() * FACTOR), 0), 
				Math.max((int) (c.getBlue() * FACTOR), 0));
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
				Math.min((int) (r / FACTOR), 255), 
				Math.min((int) (g / FACTOR), 255), 
				Math.min((int) (b / FACTOR), 255));
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
