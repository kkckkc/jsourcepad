package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;
import kkckkc.syntaxpane.model.Interval;

public class TextExecuteLineInsertingResultAction extends BaseAction {

	private WindowManager wm;
    private final Window window;

	public TextExecuteLineInsertingResultAction(Window window, WindowManager wm) {
		this.window = window;
		this.wm = wm;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		final Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();
		final Interval i = b.getSelectionOrCurrentLine();
		
		String line = b.getText(i);
		
		ScriptExecutor scriptExecutor = new ScriptExecutor(line, Application.get().getThreadPool());
		try {
	        scriptExecutor.execute(new UISupportCallback(wm.getContainer(window)) {
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
