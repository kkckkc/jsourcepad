package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;
import kkckkc.syntaxpane.model.Interval;

public class TextExecuteLineInsertingResultAction extends AbstractEditorAction {

	public TextExecuteLineInsertingResultAction(Window window) {
		super(window);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		final Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();
		final Interval i = b.getSelectionOrCurrentLine();
		
		String line = b.getText(i);
		
		ScriptExecutor scriptExecutor = new ScriptExecutor(line, Application.get().getThreadPool());
		try {
	        scriptExecutor.execute(new UISupportCallback(window.getJFrame()) {
	            public void onAfterFailure(Execution execution) {
	            	b.replaceText(i, execution.getStdout(), null);
	            }

	            public void onAfterSuccess(Execution execution) {
	            	b.replaceText(i, execution.getStderr(), null);
	            }
	        }, new StringReader(""), new HashMap<String, String>());
        } catch (IOException e1) {
        	throw new RuntimeException(e1);
        }
	}

}
