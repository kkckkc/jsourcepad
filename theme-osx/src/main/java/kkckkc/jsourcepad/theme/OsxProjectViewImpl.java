package kkckkc.jsourcepad.theme;

import java.awt.Color;

import javax.swing.plaf.TreeUI;

import kkckkc.jsourcepad.ui.ProjectViewImpl;

public class OsxProjectViewImpl extends ProjectViewImpl {

	@Override
	public void init() {
		super.init();

		setBackground(new Color(213, 221, 229));
		
        try {
			setUI((TreeUI) Class.forName("kkckkc.jsourcepad.theme.TigerTreeUI").newInstance());
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		setFont(getFont().deriveFont(11f));
	}
	
}
