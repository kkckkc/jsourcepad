package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;


import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.util.action.BaseAction;



public class TabCloseOthersAction extends BaseAction {
	private DocList docList;

	public TabCloseOthersAction(DocList docList) {
		this.docList = docList;
	}

	public void actionPerformed(ActionEvent e) {
        int tabIndex = actionContext.get(ActionContextKeys.TAB_INDEX);
		
		int i = 0;
		for (Doc doc : docList.getDocs()) {
			if (i != tabIndex) {
				doc.close();
			}
			i++;
		}
	}
}