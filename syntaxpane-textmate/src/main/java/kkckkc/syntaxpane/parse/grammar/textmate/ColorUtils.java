package kkckkc.syntaxpane.parse.grammar.textmate;

import java.awt.*;

public class ColorUtils {
	public static Color offset(Color base) {
		return offset(base, 1);
	}

	private static final double FACTOR = 0.98;

	private static Color darker(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		return new Color(
				Math.max(adjust(red, FACTOR), 0),
				Math.max(adjust(green, FACTOR), 0),
				Math.max(adjust(blue, FACTOR), 0));
	}

	private static Color brighter(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		int i = (int) (1.0 / (1.0 - FACTOR));
		if (red == 0 && green == 0 && blue == 0) {
			return new Color(i, i, i);
		}
		if (red > 0 && red < i)
			red = i;
		if (green > 0 && green < i)
			green = i;
		if (blue > 0 && blue < i)
			blue = i;

		return new Color(
				Math.min(adjust(red, 1 / FACTOR), 255),
				Math.min(adjust(green, 1 / FACTOR), 255),
				Math.min(adjust(blue, 1 / FACTOR), 255));
	}

	private static int adjust(int color, double factor, double gamma) {
		double c = ((double) color) / 255;
		double gammaCorrectedC = Math.pow(c, gamma);
		double gammaCorrectedAdjusted = gammaCorrectedC * factor;
		double adjusted = Math.pow(gammaCorrectedAdjusted, 1 / gamma);
		return (int) (adjusted * 255);
	}
		
	private static int adjust(int color, double factor) {
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
