package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.MacroManager;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BundlesMacrosStartRecordingAction extends BaseAction {

    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
	public void performAction(ActionEvent e) {
        MacroManager macroManager = window.getMacroManager();
        if (macroManager.isRecording()) {
            macroManager.stopRecording();
        } else {
            macroManager.startRecording();
        }

        if (actionContext != null) actionContext.commit();
	}

    @Override
    protected void actionContextUpdated() {
        MacroManager macroManager = window.getMacroManager();
        if (macroManager.isRecording()) {
            putValue(AbstractAction.NAME, "Stop Recording");
        } else {
            putValue(AbstractAction.NAME, "Start Recording");
        }
    }
}
