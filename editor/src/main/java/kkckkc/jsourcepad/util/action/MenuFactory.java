package kkckkc.jsourcepad.util.action;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.google.common.collect.Lists;

public class MenuFactory {
	public JPopupMenu buildPopup(final ActionGroup actionGroup, ItemBuilder itemBuilder) {
		final List<JMenuItem> items = Lists.newArrayList();

		JPopupMenu jp = new JPopupMenu();
		for (Action a : actionGroup) {
			if (a == null) {
				jp.addSeparator();
			} else if (a instanceof ActionGroup) {
				items.add(jp.add(buildMenu((String) a.getValue(AbstractAction.NAME), (ActionGroup) a, itemBuilder, true)));
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

	private void loadMenu(List<JMenuItem> items, ActionGroup actionGroup, JMenu jMenu, ItemBuilder itemBuilder, boolean lazy) {
		for (Action a : actionGroup) {
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
	
	public JMenu buildMenu(String name, final ActionGroup actionGroup, final ItemBuilder itemBuilder, final boolean lazy) {
		final List<JMenuItem> items = Lists.newArrayList();

		final JMenu jMenu = new JMenu(name);
		
		if (! lazy) {
			loadMenu(items, actionGroup, jMenu, itemBuilder, lazy);
		}
		
		jMenu.addMenuListener(new MenuListener() {
			private void loadIfRequired() {
				if (items.size() == 0) {
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
		return jMenu;
	}
	
	
	public interface ItemBuilder {
		public JMenuItem build(Action action);
	}
}
