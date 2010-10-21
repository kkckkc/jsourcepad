package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.ComponentView;
import kkckkc.syntaxpane.ScrollableSourcePane;

import javax.swing.*;

public interface DocView extends ComponentView<JComponent> {
	JComponent getComponent();

    ScrollableSourcePane getSourcePane();

	void updateTabSize(int tabSize);

	void redraw();
}
