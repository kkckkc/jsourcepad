package kkckkc.jsourcepad.theme;

import org.pushingpixels.substance.api.fonts.FontSet;

import javax.swing.plaf.FontUIResource;

public class WrapperFontSet implements FontSet {
    private int extra;
    private FontSet delegate;
    private boolean keepMenuFont;

    public WrapperFontSet(FontSet delegate, SubstanceSettings extra) {
        super();
        this.delegate = delegate;
        this.extra = extra.getFontSizeAdjustment();
        this.keepMenuFont = extra.isKeepMenuSize();
    }

    private FontUIResource getWrappedFont(FontUIResource systemFont) {
        return new FontUIResource(systemFont.getFontName(), systemFont
                .getStyle(), systemFont.getSize() + this.extra);
    }

    public FontUIResource getControlFont() {
        return this.getWrappedFont(this.delegate.getControlFont());
    }

    public FontUIResource getMenuFont() {
        if (keepMenuFont) return this.delegate.getMenuFont();
        return this.getWrappedFont(this.delegate.getMenuFont());
    }

    public FontUIResource getMessageFont() {
        return this.getWrappedFont(this.delegate.getMessageFont());
    }

    public FontUIResource getSmallFont() {
        return this.getWrappedFont(this.delegate.getSmallFont());
    }

    public FontUIResource getTitleFont() {
        return this.getWrappedFont(this.delegate.getTitleFont());
    }

    public FontUIResource getWindowTitleFont() {
        return this.getWrappedFont(this.delegate.getWindowTitleFont());
    }
}