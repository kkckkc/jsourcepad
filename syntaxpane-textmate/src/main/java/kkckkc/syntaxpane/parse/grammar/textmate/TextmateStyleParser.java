package kkckkc.syntaxpane.parse.grammar.textmate;
	
import kkckkc.syntaxpane.style.*;
import kkckkc.utils.plist.GeneralPListReader;
import kkckkc.utils.plist.PListUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
	
public class TextmateStyleParser implements kkckkc.syntaxpane.style.StyleParser {

	@Override
    public StyleScheme parse(final File file) {
	    GeneralPListReader p = new GeneralPListReader();
        try {
        	final List<?> settings = PListUtils.get(p.read(file), List.class, "settings");
			final Map<?, ?> global = PListUtils.get(settings, Map.class, 0, "settings");
			
			final Map<ScopeSelector, TextStyle> selectors = new HashMap<ScopeSelector, TextStyle>();
			for (int i = 1; i < settings.size(); i++) {
				Map<?, ?> style = (Map<?, ?>) settings.get(i);
				Map<?, ?> styleSettings = (Map<?, ?>) style.get("settings");
				
				String scope = (String) style.get("scope");
				if (scope == null) continue;

                Color fg = color(styleSettings, "foreground");
                if (fg == null) fg = color(global, "foreground");
				selectors.put(ScopeSelector.parse(scope), new StyleBean(
							fg, null,
							isStyle(styleSettings, "bold"),
							isStyle(styleSettings, "italic"),
							isStyle(styleSettings, "underline")));
			}

			final TextStyle textStyle = new StyleBean(
					color(global, "foreground"), 
					color(global, "background"));
			
			final Style selectionStyle = new StyleBean(color(global, "foreground"), color(global, "selection"));
			final Style lineNumberStyle = new StyleBean(
					ColorUtils.offset(color(global, "foreground"), 3), 
					ColorUtils.offset(color(global, "background"), 2),
					ColorUtils.offset(color(global, "background"), 3));
			
			final Color caretColor = color(global, "caret");
			final Color lineSelectionColor = color(global, "lineHighlight");
			final Color invisibles = color(global, "invisibles");
				
			final Style rightMarginStyle = new StyleBean(
					ColorUtils.offset(color(global, "background"), 2), 
					ColorUtils.offset(color(global, "background"), 1)		
			);
			
        	return new StyleScheme() {
        		public File getSource() { return file; }
				public TextStyle getTextStyle() { return textStyle; }
				public Style getSelectionStyle() { return selectionStyle; }
				public Style getLineNumberStyle() { return lineNumberStyle; }
				public Style getRightMargin() { return rightMarginStyle; }

				public Color getCaretColor() { return caretColor; }
				public Color getInvisiblesColor() { return invisibles; }
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

			return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, i2);
			
		} else {
			return Color.decode(string.substring(0, 7));	
		}
    }
}
