package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.ComponentView;
import kkckkc.syntaxpane.ScrollableSourcePane;
import kkckkc.syntaxpane.style.StyleScheme;

public interface DocView extends ComponentView<ScrollableSourcePane> {
	ScrollableSourcePane getComponent();

	void updateTabSize(int tabSize);
	void setStyleScheme(StyleScheme styleScheme);

	void redraw();

}
