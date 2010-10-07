package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.util.action.BaseAction;



public class TabCloseAllAction extends BaseAction {
	private final DocList docList;

	public TabCloseAllAction(DocList docList) {
		this.docList = docList;
	}

	public void actionPerformed(ActionEvent e) {
		for (Doc b : docList.getDocs()) {
			b.close();
		}
	}
}