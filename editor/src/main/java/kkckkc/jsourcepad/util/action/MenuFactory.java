package kkckkc.jsourcepad.util.action;

import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.List;

public class MenuFactory {
	public JPopupMenu buildPopup(final ActionGroup actionGroup, ItemBuilder itemBuilder) {
		final List<JMenuItem> items = Lists.newArrayList();

		JPopupMenu jp = new JPopupMenu();
		for (Action a : actionGroup.getItems()) {
			if (a == null) {
				jp.addSeparator();
			} else if (a instanceof ActionGroup) {
				items.add(jp.add(buildMenu((String) a.getValue(AbstractAction.NAME), (ActionGroup) a, itemBuilder, false)));
			} else {
				if (itemBuilder == null) {
					items.add(jp.add(a));
				} else {
					items.add(itemBuilder.build(a));
				}
			}
		}
		return jp;
	}

	public void loadMenu(List<JMenuItem> items, ActionGroup actionGroup, JMenu jMenu, ItemBuilder itemBuilder, boolean lazy) {
		for (Action a : actionGroup.getItems()) {
			if (a == null) {
				jMenu.addSeparator();
			} else if (a instanceof ActionGroup) {
				items.add(jMenu.add(buildMenu((String) a.getValue(AbstractAction.NAME), (ActionGroup) a, itemBuilder, lazy)));
			} else {
                if (a instanceof Presenter.Menu) {
                    items.add(jMenu.add(((Presenter.Menu) a).getMenuItem()));
                } else if (itemBuilder == null) {
					items.add(jMenu.add(a));
				} else {
					items.add(jMenu.add(itemBuilder.build(a)));
				}
			}
		}	
	}

    public void buildMenu(final JMenu jMenu, final ActionGroup actionGroup, final ItemBuilder itemBuilder, final boolean lazy) {
        final List<JMenuItem> items = Lists.newArrayList();

        if (! lazy) {
            loadMenu(items, actionGroup, jMenu, itemBuilder, lazy);
        } else {

            jMenu.addMenuListener(new MenuListener() {
                private void loadIfRequired() {
                    if (items.isEmpty()) {
                        loadMenu(items, actionGroup, jMenu, itemBuilder, lazy);
                    }
                }

                @Override
                public void menuSelected(MenuEvent e) {
                    loadIfRequired();
                }

                @Override
                public void menuDeselected(MenuEvent e) {
                }

                @Override
                public void menuCanceled(MenuEvent e) {
                }
            });

        }

        actionGroup.registerDerivedComponent(jMenu);
    }

	public JMenu buildMenu(String name, final ActionGroup actionGroup, final ItemBuilder itemBuilder, final boolean lazy) {
		final JMenu jMenu = new JMenu(name);
        buildMenu(jMenu, actionGroup, itemBuilder, lazy);
        return jMenu;
	}
	
	
	public interface ItemBuilder {
		public JMenuItem build(Action action);
	}
}
