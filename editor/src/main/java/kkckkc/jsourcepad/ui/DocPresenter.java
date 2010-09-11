package kkckkc.jsourcepad.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.action.text.IndentAction;
import kkckkc.jsourcepad.action.text.TabAction;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.FontSettings;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.StyleSettings;
import kkckkc.jsourcepad.model.TabSettings;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.SettingsManager.Listener;
import kkckkc.jsourcepad.model.SettingsManager.Setting;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.ui.CompoundUndoManager;
import kkckkc.syntaxpane.ScrollableSourcePane;

import org.springframework.beans.factory.annotation.Autowired;

public class DocPresenter implements Presenter<DocView> {
	private ScrollableSourcePane sourcePane;
	private Doc doc;
    private ActionContext actionContext;
	
	// Collaborators
	private DocView view;
	private CompoundUndoManager undoManager;

	@Autowired
    public void setView(DocView view) {
	    this.view = view;
    }

	public DocView getView() {
	    return view;
    }
	
	@Autowired
	public void setDoc(Doc doc) {
	    this.doc = doc;
    }
	
	@PostConstruct
    public void init() {
		sourcePane = view.getComponent();

        actionContext = new ActionContext();
        actionContext.put(ActionContextKeys.ACTIVE_DOC, doc);
        actionContext.put(ActionContextKeys.FOCUSED_COMPONENT, this);
        actionContext.commit();
        
        ActionContext.set(sourcePane, actionContext);

        doc.getDocList().getWindow().topic(Doc.InsertionPointListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, ACTION_CONTEXT_UPDATER);
        doc.getDocList().getWindow().topic(Doc.StateListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, ACTION_CONTEXT_UPDATER);

 		doc.getActiveBuffer().bind(sourcePane.getEditorPane());

		Application app = Application.get();
		
		app.getSettingsManager().subscribe(new SettingsListener(), false, app, doc);

		undoManager = new CompoundUndoManager(sourcePane.getEditorPane());
		sourcePane.getEditorPane().getKeymap().addActionForKeyStroke(
				KeyStroke.getKeyStroke("ctrl Z"),
				new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
	                    undo();
                    }
				});
		sourcePane.getEditorPane().getKeymap().addActionForKeyStroke(
				KeyStroke.getKeyStroke("ctrl Y"),
				new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                    	redo();
                    }
				});
		
		sourcePane.getEditorPane().getKeymap().addActionForKeyStroke(
				KeyStroke.getKeyStroke((char) KeyEvent.VK_ENTER), new IndentAction(doc));

		sourcePane.getEditorPane().getKeymap().removeKeyStrokeBinding(
				KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
		sourcePane.getEditorPane().getKeymap().addActionForKeyStroke(
				KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), new TabAction(doc));

		sourcePane.getEditorPane().requestFocus();
		
		sourcePane.setFont(Application.get().getSettingsManager().get(FontSettings.class).asFont());
    }
	
	public String getTitle() {
		return doc.getTitle();
	}


	
	@SuppressWarnings("unchecked")
    private final class SettingsListener implements Listener {
	    private SettingsListener() {
		    // Init
			settingUpdated(Application.get().getSettingsManager().get(TabSettings.class));
			settingUpdated(Application.get().getSettingsManager().get(StyleSettings.class));
			settingUpdated(Application.get().getSettingsManager().get(FontSettings.class));
	    }

	    @Override
	    public void settingUpdated(Setting settings) {
	        if (settings instanceof TabSettings) {
	        	TabSettings tabSettings = (TabSettings) settings;
	        	view.updateTabSize(tabSettings.getTabSize());
	        } else if (settings instanceof StyleSettings) {
	        	StyleSettings styleSettings = (StyleSettings) settings;
	        	view.setStyleScheme(Application.get().getStyleScheme(styleSettings));
	        } else if (settings instanceof FontSettings) {
	        	FontSettings fontSettings = (FontSettings) settings;
	        	view.getComponent().setFont(fontSettings.asFont());
	        }

	        view.redraw();
	    }
    }


	public void undo() {
		if (undoManager.canUndo()) undoManager.undo();
    }

	public void redo() {
		if (undoManager.canRedo()) undoManager.redo();
	}
	
	public void cut() {
		sourcePane.getEditorPane().cut();
	}
	
	public void copy() {
		sourcePane.getEditorPane().copy();
	}
	
	public void paste() {
		sourcePane.getEditorPane().paste();
	}


    private class ActionContextUpdater implements Doc.InsertionPointListener, Doc.StateListener {
        @Override
        public void update(InsertionPoint insertionPoint) {
//            actionContext.put(ActionContextKeys.ACTIVE_DOC, doc);
//            actionContext.commit();
        }

        @Override
        public void modified(Doc doc) {
            actionContext.put(ActionContextKeys.ACTIVE_DOC, doc);
            actionContext.commit();
        }
    };
    private ActionContextUpdater ACTION_CONTEXT_UPDATER = new ActionContextUpdater();
}
