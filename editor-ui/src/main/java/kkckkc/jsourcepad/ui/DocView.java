package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.ComponentView;
import kkckkc.syntaxpane.ScrollableSourcePane;

public interface DocView extends ComponentView<ScrollableSourcePane> {
	ScrollableSourcePane getComponent();

	void updateTabSize(int tabSize);

	void redraw();
}
