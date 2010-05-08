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
		jp.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				for (JMenuItem jmi : items) {
					Action a = jmi.getAction();
					if (a instanceof LazyAction) {
						((LazyAction) a).init();
						((LazyAction) a).activate(jmi);
					}
				}
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { 
				for (Action a : actionGroup) {
					if (a instanceof LazyAction) {
						((LazyAction) a).deactivate();
					}
				}
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) { 
				for (Action a : actionGroup) {
					if (a instanceof LazyAction) {
						((LazyAction) a).deactivate();
					}
				}
			}
		});
		return jp;
	}

	private void loadMenu(List<JMenuItem> items, ActionGroup actionGroup, JMenu jMenu, ItemBuilder itemBuilder, boolean lazy) {
		for (Action a : actionGroup) {
			if (a == null) {
				jMenu.addSeparator();
			} else if (a instanceof ActionGroup) {
				items.add(jMenu.add(buildMenu((String) a.getValue(AbstractAction.NAME), (ActionGroup) a, itemBuilder, lazy)));
			} else {
				if (itemBuilder == null) {
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
				for (JMenuItem jmi : items) {
					Action a = jmi.getAction();
					if (a instanceof LazyAction) {
						((LazyAction) a).init();
						((LazyAction) a).activate(jmi);
					}
				}
			}
			
			@Override
			public void menuDeselected(MenuEvent e) { 
				for (Action a : actionGroup) {
					if (a instanceof LazyAction) {
						((LazyAction) a).deactivate();
					}
				}
			}
			
			@Override
			public void menuCanceled(MenuEvent e) { 
				for (Action a : actionGroup) {
					if (a instanceof LazyAction) {
						((LazyAction) a).deactivate();
					}
				}
			}
		});
		return jMenu;
	}
	
	
	public interface ItemBuilder {
		public JMenuItem build(Action action);
	}
}
