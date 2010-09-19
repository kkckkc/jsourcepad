
package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.ClipboardManager;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.MenuFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

public class EditPasteFromHistoryAction extends BaseAction {
	public EditPasteFromHistoryAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);

        ClipboardManager cm = Application.get().getClipboardManager();

        ActionGroup tempActionGroup = new ActionGroup();

        List<Transferable> history = cm.getHistory();
        Collections.reverse(history);
        for (Transferable t : history) {
            tempActionGroup.add(new PasteAction(ClipboardManager.getText(t), d.getActiveBuffer()));
        }

        JPopupMenu jpm = new MenuFactory().buildPopup(tempActionGroup, null);

        // TODO: Move this into Buffer.showPopup() or something
        DocPresenter presenter = d.getPresenter(DocPresenter.class);
        Point point = presenter.getInsertionPointLocation();
        JComponent component = presenter.getComponent();

        jpm.show(component, point.x, point.y);
    }


    public static class PasteAction extends AbstractAction {
        private final String string;
        private final Buffer buffer;

        public PasteAction(String s, Buffer buffer) {
            super(s.length() > 40 ? s.substring(0, 40) : s);
            this.string = s;
            this.buffer = buffer;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            buffer.insertText(buffer.getInsertionPoint().getPosition(), string, null);
        }
    }
}

