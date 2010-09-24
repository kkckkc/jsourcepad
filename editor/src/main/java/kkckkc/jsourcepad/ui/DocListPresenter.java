package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.action.MenuFactory;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.ui.PopupUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DocListPresenter implements Presenter<DocListView>, DocList.Listener, Doc.StateListener {

	private JTabbedPane tabbedPane;

	// Collaborators
	private DocListView docListView;
	private DocList docList;
	private ActionManager actionManager;

	@Autowired
	public void setDocList(DocList docList) {
	    this.docList = docList;
    }
	
	@Autowired
	public void setActionManager(ActionManager actionManager) {
	    this.actionManager = actionManager;
    }
	
	@Autowired
    public void setView(DocListView view) {
		this.docListView = view;
    }

	@PostConstruct
    public void init() throws Exception {
		docList.getWindow().topic(DocList.Listener.class).subscribe(DispatchStrategy.SYNC, this);
		docList.getWindow().topic(Doc.StateListener.class).subscribe(DispatchStrategy.SYNC, this);
		
		tabbedPane = docListView.getTabbedPane();
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane pane = (JTabbedPane) e.getSource();
				docList.setActive(pane.getSelectedIndex());
			}
		});
		
		// Override right click selection
		final MouseListener m = tabbedPane.getMouseListeners()[0];
		tabbedPane.removeMouseListener(m);
		tabbedPane.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					m.mousePressed(e);
			}
		});
		
		
		final ActionGroup actionGroup = actionManager.createActionGroup("tab-context-menu");
		
		JPopupMenu jpm = new MenuFactory().buildPopup(actionGroup, null);
		
		PopupUtils.bind(jpm, tabbedPane, false, new PopupUtils.PopupListener() {
            public void show(MouseEvent e) {
                int tabIndex = tabbedPane.indexAtLocation((int) e.getPoint().getX(), (int) e.getPoint().getY());

                ActionContext a = actionManager.getActionContext().subContext();
                a.put(ActionContextKeys.TAB_INDEX, new Integer(tabIndex));
                a.commit();
                actionGroup.setActionContext(a);
            }
        });
		
    }	
	
	public JComponent getJComponent() {
		return tabbedPane;
	}
	
	@Override
	public void created(final Doc doc) {
		DocPresenter docPresenter = doc.getPresenter(DocPresenter.class);
		tabbedPane.add(docPresenter.getTitle(), docPresenter.getView().getComponent());
	}

	@Override
	public void selected(int index, Doc doc) {
		tabbedPane.setSelectedIndex(index);
	}

	@Override
	public void modified(Doc doc, boolean newState, boolean oldState) {
        if (newState == oldState) return;
		tabbedPane.setTitleAt(doc.getDocList().getActive(), doc.getTitle());
	}

	@Override
	public void closed(int index, Doc doc) {
		tabbedPane.removeTabAt(index);
	}
}

