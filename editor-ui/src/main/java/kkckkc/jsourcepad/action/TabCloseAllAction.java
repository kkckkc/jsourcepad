package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;



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