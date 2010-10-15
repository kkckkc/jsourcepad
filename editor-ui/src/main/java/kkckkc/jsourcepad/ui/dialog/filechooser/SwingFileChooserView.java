package kkckkc.jsourcepad.ui.dialog.filechooser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SwingFileChooserView implements FileChooserView {

	@Override
	public void openDirectory(Component parent, File pwd, FileChooserCallback fileChooserCallback) {
		JFileChooser jFileChooser = new JFileChooser(pwd);
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int ret = jFileChooser.showOpenDialog(parent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			fileChooserCallback.cancel();
		} else {
			fileChooserCallback.select(jFileChooser.getSelectedFile());
		}
	}

	@Override
	public void openFile(Component parent, File pwd, FileChooserCallback fileChooserCallback) {
		JFileChooser jFileChooser = new JFileChooser(pwd);
		int ret = jFileChooser.showOpenDialog(parent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			fileChooserCallback.cancel();
		} else {
			fileChooserCallback.select(jFileChooser.getSelectedFile());
		}
	}

	@Override
	public void saveFile(Component parent, File pwd, FileChooserCallback fileChooserCallback, boolean confirmOverwrite) {
		JFileChooser jFileChooser = new JFileChooser(pwd);
		int ret = jFileChooser.showSaveDialog(parent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			fileChooserCallback.cancel();
		} else {
			fileChooserCallback.select(jFileChooser.getSelectedFile());
		}
	}

}
