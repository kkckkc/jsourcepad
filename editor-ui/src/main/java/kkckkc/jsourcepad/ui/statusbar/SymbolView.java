package kkckkc.jsourcepad.ui.statusbar;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.SymbolList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.utils.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SymbolView extends JLabel {

	public SymbolView(final Window window) {
		setText("Function Pop-Up");
		
		final boolean allButtons = true;
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
			}

			private void maybeShowPopup(MouseEvent e) {
				if (allButtons || e.isPopupTrigger()) {
					final Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
					
					List<Pair<String, Integer>> symbols = new SymbolList(buffer).getSymbols();
					
					if (symbols == null || symbols.isEmpty()) return;
					
					JPopupMenu popupMenu = new JPopupMenu();

					for (final Pair<String, Integer> p : symbols) {
						popupMenu.add(new AbstractAction(p.getFirst()) {
                            public void actionPerformed(ActionEvent e) {
                            	buffer.setSelection(Interval.createEmpty(p.getSecond()));
                            }
						});
					}

                    popupMenu.show(SymbolView.this, e.getPoint().x, e.getPoint().y);
				}
			}

		});
	}
}
