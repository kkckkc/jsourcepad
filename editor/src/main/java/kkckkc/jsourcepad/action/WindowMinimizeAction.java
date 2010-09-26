package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;


public class WindowMinimizeAction extends BaseAction {
    private Window window;

    public WindowMinimizeAction(Window window) {
        this.window = window;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        Application.get().getWindowManager().minimize(window);
    }

}