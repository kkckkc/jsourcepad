package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.LineManager;

import java.awt.event.ActionEvent;

public class NavigationScrollAction extends BaseAction {
    private int adjustment;
    private boolean adjustLines;

    public NavigationScrollAction(int adjustment, boolean adjustLines) {
        this.adjustment = adjustment;
        this.adjustLines = adjustLines;
        
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

    @Override
    public void performAction(ActionEvent e) {
		Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
        Buffer buffer = doc.getActiveBuffer();

        int position = buffer.getTopLeftPosition();

        if (adjustLines) {
            position = adjustByLines(buffer, position);
        } else {
            LineManager lm = buffer.getLineManager();
            LineManager.Line line = lm.getLineByPosition(position);
            position += adjustment;

            if (position < line.getStart()) position = line.getStart();
            if (position > line.getEnd()) position = line.getEnd();
        }

        doc.getActiveBuffer().scrollTo(position, Buffer.ScrollAlignment.TOP);
    }

    private int adjustByLines(Buffer buffer, int position) {
        LineManager lm = buffer.getLineManager();
        LineManager.Line line = lm.getLineByPosition(position);

        if (adjustment == -1) {
            LineManager.Line newline = lm.getPrevious(line);
            return newline == null ? 0 : newline.getStart();
        } else {
            LineManager.Line newline = lm.getNext(line);
            return newline == null ? line.getStart() : newline.getStart();
        }
    }

}