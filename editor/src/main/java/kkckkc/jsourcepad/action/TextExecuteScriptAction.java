package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import javax.script.ScriptException;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;

public class TextExecuteScriptAction extends AbstractEditorAction {

	public TextExecuteScriptAction(Window window) {
		super(window);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		final Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();
		
		Interval i = b.getSelection();
		if (i == null || i.isEmpty()) {
			i = b.getCompleteDocument();
		}
		
		String line = b.getText(i);
		
        try {
	        Object o = window.getScriptEngine().eval(line);
	        if (o != null) {
	        	b.replaceText(i, o.toString(), null);
	        }
        } catch (ScriptException ex) {
	        ex.printStackTrace();
        }
	}

}
