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
		
		window.topic(DocList.Listener.class).subscribe(DispatchStrategy.EVENT_ASYNC, this);
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
		
		Application.get().getSettingsManager().subscribe(TabProjectSettings.class, this, false);
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
        updateState(doc);
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

        window.getProject().getSettingsManager().update(newSettings);
        updateLabel(newSettings);
    }


    @Override
    public void settingUpdated(TabProjectSettings settings) {
		updateLabel(settings);
    }

	private void updateLabel(TabProjectSettings settings) {
		setEnabled(true);

		StringBuilder builder = new StringBuilder();
        builder.append("Tab Width: ").append(settings.getTabSize());
		if (settings.isSoftTabs()) {
			builder.append(" (soft)");
		}
		setText(builder.toString());
		
		tabSizeButtons.get(settings.getTabSize()).setSelected(true);
		softTabsCheck.setSelected(settings.isSoftTabs());
    }
	
}
