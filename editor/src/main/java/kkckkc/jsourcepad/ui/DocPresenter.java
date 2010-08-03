package kkckkc.jsourcepad.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.action.text.IndentAction;
import kkckkc.jsourcepad.action.text.TabAction;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.FontSettings;
import kkckkc.jsourcepad.model.StyleSettings;
import kkckkc.jsourcepad.model.TabSettings;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.SettingsManager.Listener;
import kkckkc.jsourcepad.model.SettingsManager.Setting;
import kkckkc.jsourcepad.util.ui.CompoundUndoManager;
import kkckkc.syntaxpane.ScrollableSourcePane;

import org.springframework.beans.factory.annotation.Autowired;

public class DocPresenter implements Presenter<DocView> {
	private ScrollableSourcePane sourcePane;
	private Doc doc;
	
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
		
		
		WindowPresenter windowPresenter = doc.getDocList().getWindow().getPresenter(WindowPresenter.class);
		windowPresenter.bindFocus(sourcePane.getEditorPane(), Window.FocusedComponentType.DOCUMENT);
		
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
}
