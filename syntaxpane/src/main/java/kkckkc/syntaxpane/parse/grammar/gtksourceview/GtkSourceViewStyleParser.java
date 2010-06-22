package kkckkc.syntaxpane.parse.grammar.gtksourceview;

import static kkckkc.syntaxpane.util.DomUtil.getChildren;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.style.ScopeSelector;
import kkckkc.syntaxpane.style.Style;
import kkckkc.syntaxpane.style.StyleBean;
import kkckkc.syntaxpane.style.StyleScheme;
import kkckkc.syntaxpane.style.TextStyle;
import kkckkc.syntaxpane.util.DomUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;



public class GtkSourceViewStyleParser implements kkckkc.syntaxpane.style.StyleParser {

	private LanguageManager languageManager;
	
	public GtkSourceViewStyleParser(LanguageManager languageManager) {
		this.languageManager = languageManager;
	}

	public StyleScheme parse(final File file) {
		try {
			Document doc = DomUtil.parse(new InputSource(new FileInputStream(file)));
	
			Map<String, Color> colors = new HashMap<String, Color>();
			for (Element e : getChildren(doc.getDocumentElement(), "color")) {
				colors.put(e.getAttribute("name"), Color.decode(e.getAttribute("value"))); 	
			}
			colors.put("#black", Color.black);
	
			final StyleBean textStyle = new StyleBean(Color.black, Color.white, false, false, false);
			final StyleBean caretStyle = new StyleBean(Color.black, Color.white, false, false, false);
			final StyleBean selectionStyle = new StyleBean(Color.black, Color.white, false, false, false);
			final StyleBean lineNumberStyle = new StyleBean(Color.black, Color.white, false, false, false);
			final StyleBean rightMarginStyle = new StyleBean(Color.black, Color.white, false, false, false);
			
	        final Map<ScopeSelector, TextStyle> dest = new HashMap<ScopeSelector, TextStyle>();
			for (Element e : getChildren(doc.getDocumentElement(), "style")) {
				String name = e.getAttribute("name");
				if (name.indexOf(':') >= 0) {
					String langpart = name.substring(0, name.indexOf(':'));
					GtkSourceViewLanguage language = (GtkSourceViewLanguage) languageManager.getLanguage(langpart);
					name = language.resolveStyle(name.substring(name.indexOf(':') + 1));
					
					dest.put(
							ScopeSelector.parse(name), 
							new StyleBean(
									colors.get(e.getAttribute("foreground")),
									colors.get(e.getAttribute("background")),
									"true".equals(e.getAttribute("bold")),
									"true".equals(e.getAttribute("italic")),
									"true".equals(e.getAttribute("underline"))
							));
				} else if ("text".equals(name)) {
					textStyle.setBackground(colors.get(e.getAttribute("background")));
					textStyle.setColor(colors.get(e.getAttribute("foreground")));
				} else if ("cursor".equals(name)) {
					caretStyle.setColor(colors.get(e.getAttribute("foreground")));
				} else if ("selection".equals(name)) {
					selectionStyle.setBackground(colors.get(e.getAttribute("background")));
					selectionStyle.setColor(colors.get(e.getAttribute("foreground")));
				} else if ("line-numbers".equals(name)) {
					lineNumberStyle.setBackground(colors.get(e.getAttribute("background")));
					lineNumberStyle.setColor(colors.get(e.getAttribute("foreground")));
				} else if ("right-margin".equals(name)) {
					rightMarginStyle.setColor(colors.get(e.getAttribute("background")));
				}
			}
        
			return new StyleScheme() {
				public File getSource() { return file; }
				
				public Map<ScopeSelector, TextStyle> getStyles() {
					return dest;
				}
	
				public TextStyle getTextStyle() {
					return textStyle;
				}
	
				public Color getInvisiblesColor() {
					return textStyle.getColor();
				}
				
				public Color getCaretColor() {
					return caretStyle.getColor();
				}
	
				public Style getSelectionStyle() {
					return selectionStyle;
				}
	
				public Style getLineNumberStyle() {
					return lineNumberStyle;
				}
	
				public Style getRightMargin() {
					return new StyleBean(rightMarginStyle.getColor(), textStyle.getBackground());
				}

                public Color getLineSelectionColor() {
	                return textStyle.getBackground().brighter().brighter();
                }
			};
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
