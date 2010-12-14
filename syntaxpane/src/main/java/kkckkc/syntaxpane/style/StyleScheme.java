package kkckkc.syntaxpane.style;

import java.awt.*;
import java.io.File;
import java.util.Map;


public interface StyleScheme {
	public File getSource();
	
	public Map<ScopeSelector, TextStyle> getStyles();
	
	public TextStyle getTextStyle();
	public Style getSelectionStyle();
	public Style getLineNumberStyle();
	
	public Style getRightMargin();
	public Color getCaretColor();
	public Color getLineSelectionColor();
	public Color getInvisiblesColor();
}
