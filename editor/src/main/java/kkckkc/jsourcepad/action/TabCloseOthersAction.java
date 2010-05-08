package kkckkc.jsourcepad.action;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.util.ui.PopupUtils;



public class TabCloseOthersAction extends AbstractAction {
	private DocList docList;

	public TabCloseOthersAction(DocList docList) {
		this.docList = docList;
	}

	public void actionPerformed(ActionEvent e) {
		Point p = PopupUtils.getRequestedLocation(e);
		int tabIndex = ((JTabbedPane) PopupUtils.getInvoker(e)).indexAtLocation((int) p.getX(), (int) p.getY());
		
		int i = 0;
		for (Doc doc : docList.getDocs()) {
			if (i != tabIndex) {
				doc.close();
			}
			i++;
		}
	}
}