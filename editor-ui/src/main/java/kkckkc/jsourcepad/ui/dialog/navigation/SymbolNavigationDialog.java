package kkckkc.jsourcepad.ui.dialog.navigation;

import kkckkc.jsourcepad.model.SymbolList;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.utils.Pair;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SymbolNavigationDialog extends NavigationDialog {
    private SymbolList symbolList;

    public void show() {
        symbolList = new SymbolList(window.getDocList().getActiveDoc().getActiveBuffer());
        super.show();
    }
    
	public void valueChanged(ListSelectionEvent e) {
		Pair<String, Integer> obj = (Pair<String, Integer>) view.getResult().getSelectedValue();
		if (obj == null) {
			view.getPath().setText("Symbol: ");
		} else {
			view.getPath().setText("Symbol: " + obj.getFirst());
		}
	}

    @Override
    public void init() {
        super.init();

        view.getResult().setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

                return super.getListCellRendererComponent(list, ((Pair<String, Integer>) value).getFirst(), index, isSelected, cellHasFocus);
            }
        });
    }

    @Override
    public void keyReleased(KeyEvent e) {
		if (e.getSource() instanceof JTextField) {
			if (view.getTextField().getText().length() > 0) {
				view.getResult().setListData(symbolList.getSymbols(view.getTextField().getText()).toArray());
				view.getResult().setSelectedIndex(0);
			} else {
				view.getResult().setListData(new Object[] {});
			}
		}
    }

	@Override
    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			moveToSymbol();
		}
    }

	private void moveToSymbol() {
	    if (view.getResult().getSelectedValue() == null) return;

        Pair<String, Integer> obj = (Pair<String, Integer>) view.getResult().getSelectedValue();
        window.getDocList().getActiveDoc().getActiveBuffer().setSelection(Interval.createEmpty(obj.getSecond()));
	    close();
    }

    public void onEnter() {
        moveToSymbol();
    }

}