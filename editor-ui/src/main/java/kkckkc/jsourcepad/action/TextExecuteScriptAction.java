package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import javax.script.ScriptException;
import java.awt.event.ActionEvent;

public class TextExecuteScriptAction extends BaseAction {
    private final Window window;

	public TextExecuteScriptAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		final Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();
		
		Interval selection = activeBuffer.getSelection();
		if (selection == null || selection.isEmpty()) {
			selection = activeBuffer.getCompleteDocument();
		}
		
		String line = activeBuffer.getText(selection);
		
        try {
	        Object o = window.getScriptEngine().eval(line);
	        if (o != null) {
	        	activeBuffer.replaceText(selection, o.toString(), null);
	        }
        } catch (ScriptException ex) {
	        ex.printStackTrace();
        }
	}

}
