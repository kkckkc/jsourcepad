package kkckkc.syntaxpane.parse.grammar.textmate;
	
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kkckkc.syntaxpane.style.ScopeSelector;
import kkckkc.syntaxpane.style.Style;
import kkckkc.syntaxpane.style.StyleBean;
import kkckkc.syntaxpane.style.StyleScheme;
import kkckkc.syntaxpane.style.TextStyle;
import kkckkc.syntaxpane.util.plist.GeneralPListReader;
import kkckkc.syntaxpane.util.plist.PListUtils;
	
public class TextmateStyleParser implements kkckkc.syntaxpane.style.StyleParser {

	@Override
    public StyleScheme parse(final File file) {
	    GeneralPListReader p = new GeneralPListReader();
        try {
        	final List<?> settings = PListUtils.get(p.read(file), List.class, "settings"); 
			final Map<?, ?> global = PListUtils.get(settings, Map.class, (Integer) 0, "settings");
			
			final Map<ScopeSelector, TextStyle> selectors = new HashMap<ScopeSelector, TextStyle>();
			for (int i = 1; i < settings.size(); i++) {
				Map<?, ?> style = (Map<?, ?>) settings.get(i);
				Map<?, ?> styleSettings = (Map<?, ?>) style.get("settings");
				selectors.put(ScopeSelector.parse((String) style.get("scope")), new StyleBean(
							color(styleSettings, "foreground"), null,
							isStyle(styleSettings, "bold"),
							isStyle(styleSettings, "italic"),
							isStyle(styleSettings, "underline")));
			}					

			final TextStyle textStyle = new StyleBean(color(global, "foreground"), color(global, "background"));
			final Style selectionStyle = new StyleBean(color(global, "foreground"), color(global, "selection"));
			final Style lineNumberStyle = new StyleBean(color(global, "foreground"), color(global, "background"));
			final Color caretColor = color(global, "caret");
			final Color lineSelectionColor = color(global, "lineHighlight");
				
        	return new StyleScheme() {
        		public File getSource() { return file; }
				public TextStyle getTextStyle() { return textStyle; }
				public Style getSelectionStyle() { return selectionStyle; }
				public Style getLineNumberStyle() { return lineNumberStyle; }

				public Color getCaretColor() { return caretColor; }
				public Color getRightMarginColor() { return lineSelectionColor; }
                public Color getLineSelectionColor() { return lineSelectionColor; }
				public Map<ScopeSelector, TextStyle> getStyles() { return selectors; }
			};
			
        } catch (IOException e) {
	        throw new RuntimeException(e);
        }
    }

	private boolean isStyle(Map<?, ?> styleSettings, String string) {
		String s = (String) styleSettings.get("fontStyle");
		if (s == null) return false;
		
		return s.indexOf(string) >= 0;
    }
		
	private Color color(Map m, String string) {
		return makeColor((String) m.get(string));
	}
	
	private Color makeColor(String string) {
		if (string == null) return null;

		if (string.length() > 7) {
			Integer i = Integer.decode("#" + string.substring(1, 7));
			Integer i2 = Integer.decode("#" + string.substring(7));

			return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF); //, i2);
			
		} else {
			return Color.decode(string.substring(0, 7));	
		}
		
    }
}
