package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NavigationGoToTabsActionGroup extends ActionGroup implements DocList.Listener, Doc.StateListener {
    private Window window;

    @Autowired
    public NavigationGoToTabsActionGroup(Window window) {
        super("Go to Tab");

        this.window = window;
        window.topic(DocList.Listener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);
        window.topic(Doc.StateListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);

        for (Doc doc : window.getDocList().getDocs()) {
            this.items.add(new GoToTabAction(doc));
        }
        updateKeyBindings();
        updateDerivedComponents();
    }

    @Override
    public void created(Doc doc) {
        this.items.add(new GoToTabAction(doc));
        updateKeyBindings();
        updateDerivedComponents();
    }

    @Override
    public void closed(int index, Doc doc) {
        this.items.remove(index);
        updateKeyBindings();
        updateDerivedComponents();
    }

    private void updateKeyBindings() {
        int i = 1;
        for (Action a : items) {
            GoToTabAction goToTabAction = (GoToTabAction) a;
            goToTabAction.setIndex(i >= 10 ? -1 : i);
            i++;
        }
    }

    @Override
    public void selected(int index, Doc doc) {
    }

    @Override
    public void modified(Doc doc, boolean newState, boolean oldState) {
        if (newState != oldState) {
            for (Action a : items) {
                GoToTabAction gta = (GoToTabAction) a;
                if (gta.getDoc() == doc) {
                    gta.putValue(NAME, doc.getTitle());
                }
            }
            updateDerivedComponents();
        }
    }


    public class GoToTabAction extends BaseAction {
        private Doc doc;

        public GoToTabAction(Doc doc) {
            this.doc = doc;
            putValue(NAME, doc.getTitle());
        }

        public Doc getDoc() {
            return doc;
        }

        @Override
        public void performAction(ActionEvent e) {
            window.getDocList().setActive(doc);
        }

        public void setIndex(int idx) {
            if (idx == -1) {
                putValue(ACCELERATOR_KEY, null);
            } else {
                acceleratorManager = window.getAcceleratorManager();
                KeyStroke ks = acceleratorManager.parseKeyStroke("vcmd " + idx);
                if (ks != null) {
                    putValue(ACCELERATOR_KEY, ks);
                }
            }
        }
    }
}
