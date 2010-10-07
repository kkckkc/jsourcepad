package kkckkc.jsourcepad.action;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.utils.PerformanceLogger;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.syntaxpane.parse.grammar.Language;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;


public class LanguageActionGroup extends ActionGroup {

	public LanguageActionGroup(Application application) {
        PerformanceLogger.get().enter(this, "constructor");
		Map<String, ActionGroup> keyedActionGroups = Maps.newLinkedHashMap();
		for (char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
			ActionGroup ag = new ActionGroup(Character.toString(c));
			keyedActionGroups.put(Character.toString(c), ag);
		}
		
		for (final Language l : application.getLanguageManager().getLanguages()) {
			if (! l.isStandalone()) continue;
			
			ActionGroup ag = keyedActionGroups.get(Character.toString(l.getName().charAt(0)).toUpperCase());
			ag.add(new LanguageAction(application, l));
		}

		for (Map.Entry<String, ActionGroup> entry : keyedActionGroups.entrySet()) {
			if (entry.getValue().size() > 0) {
				add(entry.getValue());
			}
		}
        PerformanceLogger.get().exit();
	}

	
	public static class LanguageAction extends AbstractAction {
		private Language language;
		private Application application;

		public LanguageAction(Application application, Language language) {
			super(language.getName());
			this.language = language;
			this.application = application;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			language.compile();

			Window window = application.getWindowManager().getWindow((JComponent) e.getSource());
			window.getDocList().getActiveDoc().getActiveBuffer().setLanguage(language);
		}
	}
}
