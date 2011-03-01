package kkckkc.syntaxpane.style;

import java.awt.*;
import java.io.File;
import java.util.Map;

public class DelegatingStyleScheme implements StyleScheme {
    protected StyleScheme styleScheme;

    public DelegatingStyleScheme(StyleScheme styleScheme) {
        this.styleScheme = styleScheme;
    }

    @Override
    public File getSource() {
        return styleScheme.getSource();
    }

    @Override
    public Map<ScopeSelector, TextStyle> getStyles() {
        return styleScheme.getStyles();
    }

    @Override
    public TextStyle getTextStyle() {
        return styleScheme.getTextStyle();
    }

    @Override
    public Style getSelectionStyle() {
        return styleScheme.getSelectionStyle();
    }

    @Override
    public Style getLineNumberStyle() {
        return styleScheme.getLineNumberStyle();
    }

    @Override
    public Style getRightMargin() {
        return styleScheme.getRightMargin();
    }

    @Override
    public Color getCaretColor() {
        return styleScheme.getCaretColor();
    }

    @Override
    public Color getLineSelectionColor() {
        return styleScheme.getLineSelectionColor();
    }

    @Override
    public Color getInvisiblesColor() {
        return styleScheme.getInvisiblesColor();
    }
}
