package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class TextExecuteLineInsertingResultAction extends BaseAction {

    private final Window window;

	public TextExecuteLineInsertingResultAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		final Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();
		final Interval selectionOrCurrentLine = activeBuffer.getSelectionOrCurrentLine();
		
		String line = activeBuffer.getText(selectionOrCurrentLine);
		
		ScriptExecutor scriptExecutor = new ScriptExecutor(line, Application.get().getThreadPool());
		try {
	        scriptExecutor.execute(new UISupportCallback(window) {
	            public void onAfterFailure(Execution execution) {
	            	activeBuffer.replaceText(selectionOrCurrentLine, execution.getStdout(), null);
	            }

	            public void onAfterSuccess(Execution execution) {
	            	activeBuffer.replaceText(selectionOrCurrentLine, execution.getStderr(), null);
	            }
	        }, new StringReader(""), new HashMap<String, String>());
        } catch (IOException e1) {
        	throw new RuntimeException(e1);
        }
	}

}
