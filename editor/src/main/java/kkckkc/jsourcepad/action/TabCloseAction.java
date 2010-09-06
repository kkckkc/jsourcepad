package kkckkc.jsourcepad.action;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.ui.PopupUtils;



public class TabCloseAction extends BaseAction {
	private DocList docList;
	private Window window;
	private WindowManager wm;

	public TabCloseAction(Window window, WindowManager wm, DocList docList) {
		this.window = window;
		this.docList = docList;
		this.wm = wm;
	}

	public void actionPerformed(ActionEvent e) {
		Point p = PopupUtils.getRequestedLocation(e);
		if (p == null) {
			if (docList.getActiveDoc().isModified()) {
				int j = JOptionPane.showConfirmDialog(wm.getContainer(window), "Not saved");
				if (j == JOptionPane.CANCEL_OPTION) return;
			}
			docList.getActiveDoc().close();
		} else {
			int tabIndex = ((JTabbedPane) PopupUtils.getInvoker(e)).indexAtLocation((int) p.getX(), (int) p.getY());
		
			int i = 0;
			for (Doc doc : docList.getDocs()) {
				if (i == tabIndex) {
					if (doc.isModified()) {
						int j = JOptionPane.showConfirmDialog(wm.getContainer(window), "Not saved");
						if (j == JOptionPane.CANCEL_OPTION) return;
					}
					doc.close();
				}
				i++;
			}
		}
	}
}