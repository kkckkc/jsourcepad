package kkckkc.jsourcepad.model.bundle;

import java.util.HashMap;
import java.util.Map;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;

public class MacroEngine {
	public static final Map<String, MacroCommand> commands = new HashMap<String, MacroCommand>();
	
	static {
		commands.put("moveLeft:", new MacroCommand() {
            public void execute(Object argument, Window window) {
            	Buffer buf = window.getDocList().getActiveDoc().getActiveBuffer();
            	buf.setSelection(Interval.createEmpty(buf.getInsertionPoint().getPosition() - 1));
            	
            }
		});
		commands.put("moveRight:", new MacroCommand() {
            public void execute(Object argument, Window window) {
            	Buffer buf = window.getDocList().getActiveDoc().getActiveBuffer();
            	buf.setSelection(Interval.createEmpty(buf.getInsertionPoint().getPosition() + 1));
            }
		});
		commands.put("insertText:", new MacroCommand() {
            public void execute(Object argument, Window window) {
            	Buffer buf = window.getDocList().getActiveDoc().getActiveBuffer();
            	buf.insertText(buf.getInsertionPoint().getPosition(), argument.toString(), null);
            }
		});
		commands.put("indent:", new MacroCommand() {
            public void execute(Object argument, Window window) {
            	Buffer buf = window.getDocList().getActiveDoc().getActiveBuffer();
            	buf.indent(Interval.createEmpty(buf.getInsertionPoint().getPosition()));
            }
		});
		commands.put("selectAll:", new MacroCommand() {
            public void execute(Object argument, Window window) {
            	Buffer buf = window.getDocList().getActiveDoc().getActiveBuffer();
            	buf.setSelection(buf.getCompleteDocument());
            }
		});
	}
	
	public interface MacroCommand { 
		public void execute(Object argument, Window window);
	}
}
