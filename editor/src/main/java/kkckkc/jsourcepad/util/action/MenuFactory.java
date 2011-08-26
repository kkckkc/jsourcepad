package kkckkc.jsourcepad.util.action;

import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.List;

public class MenuFactory {
	public JPopupMenu buildPopup(final ActionGroup actionGroup, ItemBuilder itemBuilder) {
		JPopupMenu jp = new JPopupMenu();
		for (Action a : actionGroup.getItems()) {
			if (a == null) {
				jp.addSeparator();
			} else if (a instanceof ActionGroup) {
				jp.add(buildMenu((String) a.getValue(AbstractAction.NAME), (ActionGroup) a, itemBuilder, false));
			} else {
				if (itemBuilder == null) {
					jp.add(a);
				} else {
					itemBuilder.build(a);
				}
			}
		}
		return jp;
	}

	public void loadMenu(List<JMenuItem> items, ActionGroup actionGroup, JMenu jMenu, ItemBuilder itemBuilder, boolean lazy) {
        for (MenuListener ml : jMenu.getMenuListeners()) {
            if (ml instanceof LazyLoadingMenuListener) {
                jMenu.removeMenuListener(ml);
            }
        }

		for (Action a : actionGroup.getItems()) {
			if (a == null) {
				jMenu.addSeparator();
			} else if (a instanceof ActionGroup) {
				items.add(jMenu.add(buildMenu((String) a.getValue(AbstractAction.NAME), (ActionGroup) a, itemBuilder, lazy)));
			} else {
                if (a instanceof ActionPresenter.Menu) {
                    items.add(jMenu.add(((ActionPresenter.Menu) a).getMenuItem()));
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
            jMenu.addMenuListener(new LazyLoadingMenuListener(items, actionGroup, jMenu, itemBuilder, lazy));
        }

        actionGroup.registerDerivedMenu(jMenu);
    }

	public JMenu buildMenu(String name, final ActionGroup actionGroup, final ItemBuilder itemBuilder, final boolean lazy) {
		final JMenu jMenu = new JMenu(name);
        buildMenu(jMenu, actionGroup, itemBuilder, lazy);
        return jMenu;
	}
	
	
	public interface ItemBuilder {
		public JMenuItem build(Action action);
	}

    public class LazyLoadingMenuListener implements MenuListener {
        private final List<JMenuItem> items;
        private final ActionGroup actionGroup;
        private final JMenu jMenu;
        private final ItemBuilder itemBuilder;
        private final boolean lazy;

        public LazyLoadingMenuListener(List<JMenuItem> items, ActionGroup actionGroup, JMenu jMenu, ItemBuilder itemBuilder, boolean lazy) {
            this.items = items;
            this.actionGroup = actionGroup;
            this.jMenu = jMenu;
            this.itemBuilder = itemBuilder;
            this.lazy = lazy;
        }

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
    }
}
