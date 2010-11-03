package kkckkc.jsourcepad.ui.statusbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.TabSettings;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.ui.PopupUtils;

import com.google.common.collect.Maps;

public class TabView extends JLabel implements DocList.Listener, ActionListener, SettingsManager.Listener<TabSettings> {

	private Window window;

	private Map<Integer, JRadioButtonMenuItem> tabSizeButtons = Maps.newHashMap();

	private JCheckBoxMenuItem softTabsCheck;
	
	public TabView(Window window) {
		this.window = window;
		
		window.topic(DocList.Listener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);
		updateState(null);
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		ButtonGroup tabSizeGroup = new ButtonGroup();

		popupMenu.add(makeTabSizeButton(tabSizeGroup, 2));
		popupMenu.add(makeTabSizeButton(tabSizeGroup, 4));
		popupMenu.add(makeTabSizeButton(tabSizeGroup, 8));
		
		popupMenu.addSeparator();
		
		softTabsCheck = new JCheckBoxMenuItem("Use spaces");
		softTabsCheck.addActionListener(this);
		
		popupMenu.add(softTabsCheck);
		
		PopupUtils.bind(popupMenu, this, true);
		
		Application.get().getSettingsManager().subscribe(TabSettings.class, this, false, Application.get());
	}

	private JMenuItem makeTabSizeButton(ButtonGroup tabSizeGroup, Integer size) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(size.toString());
		item.addActionListener(this);
		tabSizeGroup.add(item);
		tabSizeButtons.put(size, item);
		return item;
    }

	@Override
    public void closed(int index, Doc doc) {
		if (window.getDocList().getActiveDoc() == null) {
			updateState(null);
		}
    }

	@Override
    public void created(Doc doc) { 
		Application.get().getSettingsManager().subscribe(TabSettings.class, this, false, doc);
	}

	@Override
    public void selected(int index, Doc doc) {
		updateState(doc);
    }

	private void updateState(Doc doc) {
		if (doc == null) {
			setEnabled(false);
		    setText("--");
		} else {
			updateLabel(new TabSettings(
					doc.getTabManager().isSoftTabs(), 
					doc.getTabManager().getTabSize()));
		}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
		Doc doc = window.getDocList().getActiveDoc();
		
		TabSettings newSettings;
		if (e.getSource() == softTabsCheck) {
			boolean isChecked = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			newSettings = new TabSettings(isChecked, doc.getTabManager().getTabSize());
		} else {
			int size = Integer.parseInt(((JRadioButtonMenuItem) e.getSource()).getText());
			newSettings = new TabSettings(doc.getTabManager().isSoftTabs(), size);
		}
		
		doc.topic(SettingsManager.Listener.class).post().settingUpdated(newSettings);
    }

	@Override
    public void settingUpdated(TabSettings settings) {
		updateLabel(settings);
    }

	private void updateLabel(TabSettings settings) {
		setEnabled(true);

		StringBuilder b = new StringBuilder();
        b.append("Tab Width: ").append(settings.getTabSize());
		if (settings.isSoftTabs()) {
			b.append(" (soft)");
		}
		setText(b.toString());
		
		tabSizeButtons.get(settings.getTabSize()).setSelected(true);
		softTabsCheck.setSelected(settings.isSoftTabs());
    }
	
}
