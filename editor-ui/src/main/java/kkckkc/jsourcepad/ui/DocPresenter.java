package kkckkc.jsourcepad.ui;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.action.text.NewlineAction;
import kkckkc.jsourcepad.action.text.TabAction;
import kkckkc.jsourcepad.command.window.TextComponentCommand;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.*;
import kkckkc.jsourcepad.model.settings.SettingsManager.Listener;
import kkckkc.jsourcepad.model.settings.SettingsManager.Setting;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.syntaxpane.ScrollableSourcePane;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class DocPresenter implements Presenter<DocView> {
	protected ScrollableSourcePane sourcePane;
	protected Doc doc;
    protected ActionContext actionContext;
	
	// Collaborators
	protected DocView view;
    protected Window window;
    protected InsertTextCommandManager insertTextCommandManager;

    private List<Subscription> subscriptions = Lists.newArrayList();

    @Autowired
    public void setView(DocView view) {
	    this.view = view;
    }

	public DocView getView() {
	    return view;
    }

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Autowired
	public void setDoc(Doc doc) {
	    this.doc = doc;
    }

    @Autowired
    public void setInsertTextCommandManager(InsertTextCommandManager insertTextCommandManager) {
        this.insertTextCommandManager = insertTextCommandManager;
    }

    @PreDestroy
    public void destroy() {
        for (Subscription subscription : subscriptions) subscription.unsubscribe();
    }

	@PostConstruct
    public void init() {
		sourcePane = view.getSourcePane();

        JEditorPane editorPane = sourcePane.getEditorPane();
        doc.getActiveBuffer().bind(editorPane);

		Application app = Application.get();

        subscriptions.add(app.getSettingsManager().subscribe(Setting.class, new SettingsListener(), false));

        SettingsManager sm = window.getProject().getSettingsManager();
        subscriptions.add(sm.subscribe(Setting.class, new ProjectSettingsListener(sm), false));

        editorPane.getActionMap().put("jsourcepad-newline", new NewlineAction());
        editorPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(new Character('\n'), 0), "none");
        editorPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "jsourcepad-newline");

        editorPane.getActionMap().put("jsourcepad-tab", new TabAction());
        editorPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(new Character('\t'), 0), "none");
        editorPane.getInputMap(JComponent.WHEN_FOCUSED).remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        editorPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "jsourcepad-tab");

        editorPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "caret-begin-line");
        editorPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "caret-end-line");

		editorPane.requestFocus();
		
		sourcePane.setFont(Application.get().getSettingsManager().get(FontSettings.class).asFont());

        wrapClipboardAction("copy", sourcePane);
        wrapClipboardAction("cut", sourcePane);

        editorPane.getKeymap().setDefaultAction(
                insertTextCommandManager.getDefaultTypedAction(editorPane.getKeymap().getDefaultAction()));

        wrapAllActions(sourcePane);

        actionContext = new ActionContext();
        actionContext.put(ActionContextKeys.ACTIVE_DOC, doc);
        actionContext.put(ActionContextKeys.FOCUSED_COMPONENT, doc);
        actionContext.commit();

        subscriptions.add(doc.getDocList().getWindow().topic(Buffer.SelectionListener.class).subscribe(DispatchStrategy.EVENT_ASYNC, ACTION_CONTEXT_UPDATER));
        subscriptions.add(doc.getDocList().getWindow().topic(Doc.StateListener.class).subscribe(DispatchStrategy.EVENT_ASYNC, ACTION_CONTEXT_UPDATER));
        
        ActionContext.set(view.getComponent(), actionContext);
    }


    private void wrapAllActions(ScrollableSourcePane sourcePane) {
        ActionMap am = sourcePane.getEditorPane().getActionMap();
        for (final Object action : am.allKeys()) {
            final Action a = am.get(action);
            if (a instanceof InsertTextCommandManager.DefaultTypedAction) continue;
            am.put(action, new BaseAction(a) {
                @Override
                protected void performAction(ActionEvent e) {
                    TextComponentCommand textComponentCommand = new TextComponentCommand();
                    textComponentCommand.setAction(action.toString());
                    textComponentCommand.setActionEvent(e);
                    window.getCommandExecutor().execute(textComponentCommand);
                }
            });
        }
    }

    public String getTitle() {
		return doc.getTitle();
	}

    private void wrapClipboardAction(String action, ScrollableSourcePane sourcePane) {
        final Action a = sourcePane.getEditorPane().getActionMap().get(action);

        sourcePane.getEditorPane().getActionMap().put(action, new BaseAction() {
            @Override
            public void performAction(ActionEvent e) {
                a.actionPerformed(e);
                Application.get().getClipboardManager().register();
            }
        });
    }

    public JComponent getComponent() {
        return this.sourcePane;
    }

    public Point getInsertionPointLocation() {
        return this.sourcePane.getEditorPane().getCaret().getMagicCaretPosition();
    }


	
	@SuppressWarnings("unchecked")
    private final class SettingsListener implements Listener {
	    private SettingsListener() {
		    // Init
			settingUpdated(Application.get().getSettingsManager().get(StyleSettings.class));
			settingUpdated(Application.get().getSettingsManager().get(FontSettings.class));
            settingUpdated(Application.get().getSettingsManager().get(GutterSettings.class));
	    }

	    @Override
	    public void settingUpdated(Setting settings) {
	        if (settings instanceof StyleSettings) {
	        	StyleSettings styleSettings = (StyleSettings) settings;
	        	view.getSourcePane().setStyleScheme(Application.get().getStyleScheme(styleSettings));
                view.getSourcePane().setShowInvisibles(styleSettings.isShowInvisibles());
                view.getSourcePane().setWrapColumn(styleSettings.getWrapColumn());
	        } else if (settings instanceof FontSettings) {
	        	FontSettings fontSettings = (FontSettings) settings;
	        	view.getSourcePane().setFont(fontSettings.asFont());
	        } else if (settings instanceof GutterSettings) {
	        	GutterSettings gutterSettings = (GutterSettings) settings;
	        	view.getSourcePane().setFoldings(gutterSettings.isFoldings());
                view.getSourcePane().setLineNumbers(gutterSettings.isLineNumbers());
	        } else {
                return;
            }

	        view.redraw();
	    }
    }

    private final class ProjectSettingsListener implements Listener {
	    private ProjectSettingsListener(SettingsManager settingsManager) {
		    // Init
			settingUpdated(settingsManager.get(TabProjectSettings.class));
			settingUpdated(settingsManager.get(EditModeProjectSettings.class));
	    }

	    @Override
	    public void settingUpdated(Setting settings) {
	        if (settings instanceof TabProjectSettings) {
	        	TabProjectSettings tabSettings = (TabProjectSettings) settings;
	        	view.updateTabSize(tabSettings.getTabSize());
	        } else if (settings instanceof EditModeProjectSettings) {
	        	EditModeProjectSettings editModeProjectSettings = (EditModeProjectSettings) settings;
	        	view.getSourcePane().setOverwriteMode(editModeProjectSettings.isOverwriteMode());
	        } else {
                return;
            }

	        view.redraw();
	    }
    }

    private class ActionContextUpdater implements Doc.StateListener, Buffer.SelectionListener {
        @Override
        public void modified(Doc doc, boolean newState, boolean oldState) {
            // TODO: Why is this needed. Why do we need to change the state for all document modifications
            actionContext.update();
        }

        @Override
        public void selectionModified(Buffer buffer) {
            if (buffer.getSelection() == null) {
                actionContext.remove(ActionContextKeys.SELECTION);
            } else {
                actionContext.put(ActionContextKeys.SELECTION, new Object[] { buffer.getSelection() });
            }
            actionContext.commit();
        }
    }

    private ActionContextUpdater ACTION_CONTEXT_UPDATER = new ActionContextUpdater();
}
