package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.View;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.io.File;

public interface ProjectView extends View {

	void insertFile(File file);

	void refresh(File file);

	void refresh();

	JComponent getJComponent();

    void revealFile(File file);

    void setModel(TreeModel treeModel);
}
