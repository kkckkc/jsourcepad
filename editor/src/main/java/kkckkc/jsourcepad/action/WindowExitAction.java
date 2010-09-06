package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import kkckkc.jsourcepad.util.action.BaseAction;

public class WindowExitAction extends BaseAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}

}
