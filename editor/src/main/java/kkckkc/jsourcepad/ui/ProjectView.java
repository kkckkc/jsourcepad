package kkckkc.jsourcepad.ui;

import java.io.File;

import javax.swing.JComponent;

import kkckkc.jsourcepad.View;

public interface ProjectView extends View {

	void insertFile(File file);

	void refresh(File file);

	void refresh();

	JComponent getJComponent();

    void revealFile(File file);

}
