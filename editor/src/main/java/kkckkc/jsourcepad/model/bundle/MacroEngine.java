package kkckkc.jsourcepad.model.bundle;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.utils.Pair;

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
		
		commands.put("deleteWordLeft:", new TextComponentActionMacroCommand("delete-previous-word"));
		commands.put("deleteWordRight:", new TextComponentActionMacroCommand("delete-next-word"));
		commands.put("deleteForward:", new TextComponentActionMacroCommand("delete-next"));
		commands.put("deleteBackward:", new TextComponentActionMacroCommand("delete-previous"));

		commands.put("moveUp:", new TextComponentActionMacroCommand("caret-up"));
		
		commands.put("moveToBeginningOfLine:", new TextComponentActionMacroCommand("carent-begin-line"));
		commands.put("moveToEndOfLine:", new TextComponentActionMacroCommand("caret-end-line"));
		commands.put("moveToEndOfParagraph:", new TextComponentActionMacroCommand("caret-end-paragraph"));
		commands.put("selectWord:", new TextComponentActionMacroCommand("select-word"));
				
		commands.put("cut:", new TextComponentActionMacroCommand("cut"));
		commands.put("copy:", new TextComponentActionMacroCommand("copy"));
		commands.put("paste:", new TextComponentActionMacroCommand("paste"));
		
		commands.put("insertNewline:", new TextComponentActionMacroCommand("insert-break"));
		
		commands.put("moveToEndOfDocumentAndModifySelection:", new TextComponentActionMacroCommand("selection-end"));
		commands.put("moveToBeginningOfDocumentAndModifySelection:", new TextComponentActionMacroCommand("selection-begin"));
		commands.put("moveRightAndModifySelection:", new TextComponentActionMacroCommand("selection-forward"));
	}	
	
	static class TextComponentActionMacroCommand implements MacroCommand {
		private String actionName;
		
		public TextComponentActionMacroCommand(String actionName) {
			this.actionName = actionName;
		}

		@Override
        public void execute(Object argument, Window window) {
	        Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();
	        
	        // TODO: Check this
	        b.getActionMap().get(actionName).actionPerformed(
	        		new ActionEvent("", 0, actionName));
        }
	}
	
	public interface MacroCommand { 
		public void execute(Object argument, Window window);
	}
	
	
	private Window window;
	
	public MacroEngine(Window window) {
	    super();
	    this.window = window;
    }

	public void execute(List<Pair<String, Object>> macro) {
		for (Pair<String, Object> statement : macro) {
			MacroCommand command = commands.get(statement.getFirst());
			if (command == null) {
				throw new RuntimeException("Command " + statement.getFirst() + " not found or implemented yet	");
			}
			command.execute(statement.getSecond(), window);
		}
	}
}
