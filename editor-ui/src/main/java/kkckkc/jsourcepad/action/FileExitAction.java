package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class FileExitAction extends BaseAction {

	@Override
	public void performAction(ActionEvent e) {
		System.exit(0);
	}

}
