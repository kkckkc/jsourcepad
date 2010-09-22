package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.TabSettings;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextConvertSpacesToTabsAction extends BaseAction {
    private final Window window;

	public TextConvertSpacesToTabsAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        TabSettings ts = Application.get().getSettingsManager().get(TabSettings.class);

		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();

		Interval i = b.getSelectionOrCurrentLine();
        String text = b.getText(i);

        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < ts.getTabSize(); j++) builder.append(" ");

        text = text.replace(builder.toString(), "\t");

		b.replaceText(i, text, null);
    }

}