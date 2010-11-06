package kkckkc.jsourcepad.ui.statusbar;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.TabProjectSettings;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.ui.PopupUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class TabView extends JLabel implements DocList.Listener, ActionListener, SettingsManager.Listener<TabProjectSettings> {

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
		
		Application.get().getSettingsManager().subscribe(TabProjectSettings.class, this, false, Application.get());
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
		Application.get().getSettingsManager().subscribe(TabProjectSettings.class, this, false, doc);
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
			updateLabel(new TabProjectSettings(
					doc.getTabManager().isSoftTabs(), 
					doc.getTabManager().getTabSize()));
		}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
		Doc doc = window.getDocList().getActiveDoc();
		
		TabProjectSettings newSettings;
		if (e.getSource() == softTabsCheck) {
			boolean isChecked = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			newSettings = new TabProjectSettings(isChecked, doc.getTabManager().getTabSize());
		} else {
			int size = Integer.parseInt(((JRadioButtonMenuItem) e.getSource()).getText());
			newSettings = new TabProjectSettings(doc.getTabManager().isSoftTabs(), size);
		}

        getSettingsManager().update(newSettings);
		
		doc.topic(SettingsManager.Listener.class).post().settingUpdated(newSettings);
    }


    private SettingsManager getSettingsManager() {
        SettingsManager settingsManager;
        if (window.getProject() != null) {
            settingsManager = window.getProject().getSettingsManager();
        } else {
            settingsManager = Application.get().getSettingsManager();
        }
        return settingsManager;
    }

	@Override
    public void settingUpdated(TabProjectSettings settings) {
		updateLabel(settings);
    }

	private void updateLabel(TabProjectSettings settings) {
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
