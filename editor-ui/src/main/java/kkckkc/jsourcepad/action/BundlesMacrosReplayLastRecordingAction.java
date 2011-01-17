package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.MacroManager;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.ActionStateRule;
import kkckkc.jsourcepad.util.action.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;

public class BundlesMacrosReplayLastRecordingAction extends BaseAction {

    private MacroManager macroManager;

    @Autowired
    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    public BundlesMacrosReplayLastRecordingAction() {
        setActionStateRules(new ActionStateRule() {
            @Override
            public boolean shouldBeEnabled(ActionContext actionContext) {
                return macroManager.hasRecordedMacro();
            }
        });
    }

	@Override
	public void performAction(ActionEvent e) {
        macroManager.getRecordedMacro().execute();
	}

}
