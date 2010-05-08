package kkckkc.jsourcepad.ui.dialog.filechooser;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class SwingFileChooserView implements FileChooserView {

	@Override
	public void openDirectory(Component parent, File pwd, FileChooserCallback fileChooserCallback) {
		JFileChooser jFileChooser = new JFileChooser(pwd);
		jFileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "Directory";
			}
			
		});
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
