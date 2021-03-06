package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;



public class TabCloseAction extends BaseAction {
	private DocList docList;
	private Window window;

	public TabCloseAction(Window window, DocList docList) {
		this.window = window;
		this.docList = docList;
	}

	public void performAction(ActionEvent e) {
        Integer tabIndex = actionContext.get(ActionContextKeys.TAB_INDEX);
		if (tabIndex == null) {
            if (docList.getActiveDoc() == null) return;
			if (docList.getActiveDoc().isModified()) {
				int j = JOptionPane.showConfirmDialog(window.getContainer(), "Not saved");
				if (j == JOptionPane.CANCEL_OPTION) return;
			}
			docList.getActiveDoc().close();
		} else {
			int i = 0;
			for (Doc doc : docList.getDocs()) {
				if (i == tabIndex) {
					if (doc.isModified()) {
						int j = JOptionPane.showConfirmDialog(window.getContainer(), "Not saved");
						if (j == JOptionPane.CANCEL_OPTION) return;
					}
					doc.close();
				}
				i++;
			}
		}
	}
}