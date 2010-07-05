package kkckkc.jsourcepad.theme;

import java.awt.Color;

import javax.swing.BorderFactory;

import kkckkc.jsourcepad.ui.ProjectViewImpl;

public class OsxProjectViewImpl extends ProjectViewImpl {

	@Override
	public void init() {
		super.init();

		setBackground(new Color(213, 221, 229));
		/*setBorder(BorderFactory.createEmptyBorder());*/
		/*
		putClientProperty(

				   "Quaqua.Tree.style", "sourceList"

				);
				*/
	}
	
}
